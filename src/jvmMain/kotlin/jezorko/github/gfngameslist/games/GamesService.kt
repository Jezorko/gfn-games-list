package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.nvidia.NVidiaApiClient
import mu.KotlinLogging.logger
import java.lang.System.currentTimeMillis

internal object GamesService {

    private val log = logger { }

    fun getGames(limit: Int = 10, page: Int = 0, titlePart: String? = null, store: Store? = null) =
        GetGamesResponse(
            supportedGamesCount = GamesRepository.countSupportedGames(),
            games = GamesRepository.getGames(limit, page, titlePart, store),
            lastUpdatedAt = LatestUpdatesRepository.lastUpdatedAt(),
        )

    fun updateIfNeeded() {
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
                            updatedAt = timestampNow
                        )
                    }
                    .forEach(GamesRepository::putGame)
                LatestUpdatesRepository.registerUpdateComplete()
                log.info { "games update complete" }
            }
        }
    }

}