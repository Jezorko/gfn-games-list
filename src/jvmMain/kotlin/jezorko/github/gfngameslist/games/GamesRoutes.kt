package jezorko.github.gfngameslist.games

import io.ktor.application.*
import io.ktor.routing.*
import jezorko.github.gfngameslist.shared.respondJson


fun Application.gamesRoutes() = routing {
    get("/api/games") {
        val limit = call.request.queryParameters["limit"]?.toInt() ?: 10
        val titlePart = call.request.queryParameters["title"]

        call.respondJson(provider = { GamesService.getGames(limit, titlePart) })
        GamesService.updateIfNeeded()
    }
}