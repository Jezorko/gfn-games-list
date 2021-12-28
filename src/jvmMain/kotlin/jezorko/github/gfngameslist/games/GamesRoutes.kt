package jezorko.github.gfngameslist.games

import io.ktor.application.*
import io.ktor.routing.*
import jezorko.github.gfngameslist.shared.respondJson

fun Application.gamesRoutes() = routing {
    get("/api/games") {
        val limit = call.request.queryParameters["limit"]?.toInt() ?: 10
        val page = call.request.queryParameters["page"]?.toInt() ?: 0
        val titlePart = call.request.queryParameters["title"]
        val launcherParam = call.request.queryParameters["store"]
        val store = if (launcherParam != null) try {
            Store.valueOf(launcherParam)
        } catch (exception: IllegalArgumentException) {
            null
        } else null
        val publisherPart = call.request.queryParameters["publisher"]

        call.respondJson(provider = { GamesService.getGames(limit, page, titlePart, store, publisherPart) })
        GamesService.updateIfNeeded()
    }
}