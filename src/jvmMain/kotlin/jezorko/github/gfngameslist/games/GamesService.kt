package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.nvidia.NVidiaApiClient
import mu.KotlinLogging.logger
import java.lang.System.currentTimeMillis
import java.util.concurrent.atomic.AtomicBoolean

internal object GamesService {

    private val log = logger { }
    private val updateOngoing = AtomicBoolean(false)

    fun getGames(limit: Int = 10, page: Int = 0, titlePart: String? = null, store: Store? = null) =
        GetGamesResponse(
            supportedGamesCount = GamesRepository.countSupportedGames(),
            games = GamesRepository.getGames(limit, page, titlePart, store),
            lastUpdatedAt = LatestUpdatesRepository.lastUpdatedAt(),
        )

    fun updateIfNeeded() {
        if (updateOngoing.getAndSet(true)) {
            log.info { "update already ongoing, skipping" }
            return
        }

        try {
            doInTransaction {
                val timestampNow = currentTimeMillis()
                if (LatestUpdatesRepository.shouldUpdate()) {
                    log.info { "games update needed, starting" }
                    (NVidiaApiClient.fetchSupportedGamesList() ?: emptyList())
                        .map { supportedGame ->
                            Game(
                                id = supportedGame.id,
                                title = supportedGame.title,
                                store = try {
                                    Store.valueOf(supportedGame.store.uppercase().replace(' ', '_'))
                                } catch (exception: IllegalArgumentException) {
                                    log.warn {
                                        "received unsupported ${supportedGame::store.name} value ${supportedGame.store} for game ${supportedGame.title}"
                                    }
                                    Store.UNKNOWN
                                },
                                launcherGameId = supportedGame.id,
                                imageUrl = supportedGame.imageUrl,
                                registeredAt = timestampNow,
                                updatedAt = timestampNow,
                                status = try {
                                    GameStatus.valueOf(supportedGame.status)
                                } catch (exception: IllegalArgumentException) {
                                    log.warn {
                                        "received unsupported ${supportedGame::status.name} value ${supportedGame.status} for game ${supportedGame.title}"
                                    }
                                    GameStatus.UNKNOWN
                                }
                            )
                        }
                        .forEach(GamesRepository::putGame)
                    LatestUpdatesRepository.registerUpdateComplete()
                    log.info { "games update complete" }
                }
            }
        } catch (exception: Exception) {
            log.error(exception) { "failed to update games!" }
        } finally {
            updateOngoing.set(false)
        }
    }

}