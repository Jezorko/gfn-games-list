package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.nvidia.NVidiaApiClient
import mu.KotlinLogging.logger
import java.lang.System.currentTimeMillis

internal object GamesService {

    private val log = logger { }

    fun getGames(limit: Int = 10, page: Int = 0, titlePart: String? = null, launcher: Launcher? = null) =
        GetGamesResponse(
            supportedGamesCount = GamesRepository.countSupportedGames(),
            games = GamesRepository.getGames(limit, page, titlePart, launcher),
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
                            title = supportedGame.name,
                            launcher = try {
                                Launcher.valueOf(supportedGame.launcher)
                            } catch (exception: IllegalArgumentException) {
                                log.warn { "received unsupported ${supportedGame::launcher.name} value ${supportedGame.launcher}" }
                                Launcher.UNKNOWN
                            },
                            launcherGameId = supportedGame.launcherGameId,
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