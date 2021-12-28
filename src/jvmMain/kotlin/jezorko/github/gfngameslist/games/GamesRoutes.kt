package jezorko.github.gfngameslist.games

import io.ktor.application.*
import io.ktor.routing.*
import jezorko.github.gfngameslist.shared.respondJson

fun Application.gamesRoutes() = routing {
    get("/api/games") {
        val limit = call.request.queryParameters["limit"]?.toInt() ?: 10
        val titlePart = call.request.queryParameters["title"]
        val launcherParam = call.request.queryParameters["launcher"]
        val launcher = if (launcherParam != null) try {
            Launcher.valueOf(launcherParam)
        } catch (exception: IllegalArgumentException) {
            null
        } else null

        call.respondJson(provider = { GamesService.getGames(limit, titlePart, launcher) })
        GamesService.updateIfNeeded()
    }
}