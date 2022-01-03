package jezorko.github.gfngameslist.games

import io.ktor.application.*
import io.ktor.routing.*
import jezorko.github.gfngameslist.shared.deserializeSet
import jezorko.github.gfngameslist.shared.respondJson

fun Application.gamesRoutes() = routing {
    get("/api/games") {
        val limit = call.request.queryParameters["limit"]?.toInt() ?: 10
        val page = call.request.queryParameters["page"]?.toInt() ?: 0
        val searchQuery = call.request.queryParameters["query"]
        val storesFilter = GameStore::class.deserializeSet(call.request.queryParameters["store"])
        val genresFilter = GameGenre::class.deserializeSet(call.request.queryParameters["genre"])

        call.respondJson(provider = { GamesService.getGames(limit, page, searchQuery, storesFilter, genresFilter) })
        GamesService.updateIfNeeded()
    }
}