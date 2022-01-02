package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.nvidia.NVidiaApiClient
import mu.KotlinLogging.logger
import java.lang.System.currentTimeMillis
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

internal object GamesService {

    private val log = logger { }

    private val lastLocalCacheUpdateTimestamp = AtomicLong(0)
    private val localGamesCache = AtomicReference<List<Game>>(emptyList())
    private val updateOngoing = AtomicBoolean(false)

    fun getGames(
        limit: Int,
        page: Int,
        titlePart: String?,
        store: GameStore?,
        publisherPart: String?,
        genrePart: String?
    ) =
        GetGamesResponse(
            supportedGamesCount = localGamesCache.get().distinctBy(Game::title).count(),
            games = localGamesCache.get()
                .asSequence()
                .filter { game -> titlePart?.let { game.title.uppercase().contains(it.uppercase()) } ?: true }
                .filter { game -> publisherPart?.let { game.publisher.uppercase().contains(it.uppercase()) } ?: true }
                .filter { game -> store?.let { game.store == store } ?: true }
                .filter { game ->
                    genrePart?.let {
                        game.genres.any { genre ->
                            genre.name.contains(it.uppercase())
                        }
                    } ?: true
                }
                .sortedBy(Game::title)
                .drop(page * limit)
                .take(limit)
                .toList(),
            lastUpdatedAt = lastLocalCacheUpdateTimestamp.get(),
        )

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
                        .flatMap { supportedGame -> supportedGame.variants.map { variant -> variant to supportedGame } }
                        .map { variantToSupportedGame ->
                            Game(
                                id = variantToSupportedGame.second.id,
                                title = variantToSupportedGame.second.title,
                                store = variantToSupportedGame.first.appStore,
                                imageUrl = variantToSupportedGame.second.imageUrl,
                                registeredAt = variantToSupportedGame.first.gfn.releaseDate?.toInstant()?.toEpochMilli()
                                    ?: timestampNow,
                                updatedAt = timestampNow,
                                status = variantToSupportedGame.first.gfn.status,
                                publisher = variantToSupportedGame.first.publisherName,
                                storeUrl = "",
                                genres = variantToSupportedGame.second.genres
                            )
                        }.toList()
                    games.forEach(GamesRepository::putGame)
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