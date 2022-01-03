package jezorko.github.gfngameslist.games

import io.ktor.application.*
import io.ktor.routing.*
import jezorko.github.gfngameslist.shared.deserializeSet
import jezorko.github.gfngameslist.shared.respondJson

fun Application.gamesRoutes() = routing {
    get("/api/games") {
        val limit = call.request.queryParameters["limit"]?.toInt() ?: 10
        val page = call.request.queryParameters["page"]?.toInt() ?: 0
        val titlePart = call.request.queryParameters["title"]
        val storeParam = call.request.queryParameters["store"]
        val store = if (storeParam != null) try {
            GameStore.valueOf(storeParam)
        } catch (exception: IllegalArgumentException) {
            null
        } else null
        val publisherPart = call.request.queryParameters["publisher"]
        val genresFilter = GameGenre::class.deserializeSet(call.request.queryParameters["genre"])

        call.respondJson(provider = { GamesService.getGames(limit, page, titlePart, store, publisherPart, genresFilter) })
        GamesService.updateIfNeeded()
    }
}