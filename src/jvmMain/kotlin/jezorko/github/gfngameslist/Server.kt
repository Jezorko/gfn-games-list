package jezorko.github.gfngameslist

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import jezorko.github.gfngameslist.database.databaseRoutes
import jezorko.github.gfngameslist.games.GamesFacade
import jezorko.github.gfngameslist.games.gamesRoutes
import jezorko.github.gfngameslist.localization.localizationRoutes
import jezorko.github.gfngameslist.shared.Configuration
import kotlinx.html.*
import mu.KotlinLogging.logger

val log = logger { }

fun HTML.index() {
    head {
        title("GeForce NOW Supported Games List")
    }
    body {
        div {
            id = "root"
        }
        script(src = "/static/gfn-games-list.js") {}
    }
}

fun main() {
    log.info { "admin token is ${Configuration.ADMIN_TOKEN.value}" }

    GamesFacade.updateIfNeeded()

    embeddedServer(Netty, port = Configuration.PORT.value) {
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
            static("/static") {
                resources()
            }
        }.mergeAll(
            localizationRoutes(),
            gamesRoutes(),
            databaseRoutes()
        )
    }.start(wait = true)
}

fun <TSubject : Any, TContext : Any> Pipeline<TSubject, TContext>.mergeAll(
    vararg pipelines: Pipeline<TSubject, TContext>
) {
    pipelines.forEach(this::merge)
}
