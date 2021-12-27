package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.nvidia.NVidiaApiClient
import mu.KotlinLogging.logger

internal object GamesService {

    private val log = logger { }

    fun getGames(limit: Int = 10, titlePart: String? = null) = GamesRepository.getGames(limit, titlePart)

    suspend fun updateIfNeeded() {
        doInTransaction {
            if (LatestUpdatesRepository.shouldUpdate()) {
                log.info { "games update needed, starting" }
                (NVidiaApiClient.fetchSupportedGamesList() ?: emptyList())
                    .map { supportedGame ->
                        Game(
                            title = supportedGame.name,
                            launcher = supportedGame.launcher,
                            launcherGameId = supportedGame.launcherGameId,
                            imageUrl = supportedGame.imageUrl
                        )
                    }
                    .forEach(GamesRepository::putGame)
                LatestUpdatesRepository.registerUpdateComplete()
                log.info { "games update complete" }
            }
        }
    }

}