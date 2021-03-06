package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.nvidia.NVidiaApiClient
import jezorko.github.gfngameslist.nvidia.SupportedGameVariant
import mu.KotlinLogging.logger
import java.lang.System.currentTimeMillis
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

internal object GamesService {

    private val log = logger { }

    private val lastLocalCacheUpdateTimestamp = AtomicLong(0)
    private val localGamesCache = AtomicReference<Collection<Game>>(emptyList())
    private val updateOngoing = AtomicBoolean(false)

    fun getGames(
        limit: Int,
        page: Int,
        searchQuery: String?,
        storesFilter: Set<GameStore>,
        genresFilter: Set<GameGenre>,
        keywordsFilter: Set<String>?
    ) = localGamesCache.get().let { gamesFromCache ->
        val games = gamesFromCache.ifEmpty { GamesRepository.findAll().also(localGamesCache::set) }

        GetGamesResponse(
            supportedGamesCount = games.distinctBy(Game::title).count(),
            games = games
                .asSequence()
                .filter { game -> storesFilter.isEmpty() || game.stores.containsAll(storesFilter) }
                .filter { game -> genresFilter.isEmpty() || game.genres.containsAll(genresFilter) }
                .filter { game ->
                    keywordsFilter?.map(String::uppercase)?.all { keyword ->
                        game.keywords
                            .replace('_', ' ')
                            .replace('-', ' ')
                            .uppercase()
                            .contains(keyword)
                    } ?: true
                }
                .filter { game ->
                    searchQuery?.uppercase()?.let {
                        listOf(game.title, game.publisher, game.keywords)
                            .map(String::uppercase)
                            .any { textFragment -> textFragment.contains(it) }
                    } ?: true
                }
                .sortedBy(Game::title)
                .drop(page * limit)
                .take(limit)
                .toList(),
            lastUpdatedAt = lastLocalCacheUpdateTimestamp.get(),
        )
    }

    fun updateIfNeeded() {
        if (updateOngoing.getAndSet(true)) {
            log.info { "update already ongoing, skipping" }
            return
        }

        try {
            doInTransaction {
                val latestUpdateTimestamp = LatestUpdatesRepository.lastUpdatedAt()
                val timestampNow = currentTimeMillis()
                if (LatestUpdatesRepository.shouldUpdate()) {
                    log.info { "games update needed, starting" }
                    val games = NVidiaApiClient.fetchSupportedGamesList()
                        .map { supportedGame ->
                            Game(
                                id = supportedGame.id,
                                title = supportedGame.title,
                                stores = supportedGame.variants.map(SupportedGameVariant::appStore).toSet(),
                                imageUrl = supportedGame.imageUrl,
                                registeredAt = supportedGame.variants.firstOrNull()?.gfn?.releaseDate?.toInstant()
                                    ?.toEpochMilli()
                                    ?: timestampNow,
                                updatedAt = timestampNow,
                                status = supportedGame.variants.firstOrNull()?.gfn?.status ?: GameStatus.UNKNOWN,
                                publisher = supportedGame.publisherName,
                                storeUrl = "",
                                genres = supportedGame.genres,
                                regions = supportedGame.supportedRegions,
                                keywords = supportedGame.computedValues.allKeywords.joinToString(", ")
                            )
                        }
                    GamesRepository.updateGames(games)
                    LatestUpdatesRepository.registerUpdateComplete(timestampNow)
                    localGamesCache.set(games)
                    lastLocalCacheUpdateTimestamp.set(timestampNow)
                    log.info { "games update complete" }
                } else if (
                    localGamesCache.get().isEmpty()
                    || lastLocalCacheUpdateTimestamp.get() != latestUpdateTimestamp
                ) {
                    log.info { "no database update needed but local cache needs updating" }
                    val games = GamesRepository.findAll()
                    localGamesCache.set(games)
                    lastLocalCacheUpdateTimestamp.set(latestUpdateTimestamp)
                    log.info { "local cache updated" }
                }
            }
        } catch (exception: Exception) {
            log.error(exception) { "failed to update games!" }
        } finally {
            updateOngoing.set(false)
        }
    }

}