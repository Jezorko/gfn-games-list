package jezorko.github.gfngameslist

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import jezorko.github.gfngameslist.games.gamesRoutes
import jezorko.github.gfngameslist.localization.localizationRoutes
import jezorko.github.gfngameslist.shared.Configuration
import kotlinx.html.*

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor"
        }
        div {
            id = "root"
        }
        script(src = "/static/gfn-games-list.js") {}
    }
}

fun main() {


    embeddedServer(Netty, port = Configuration.SERVER_PORT.value, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
            static("/static") {
                resources()
            }
        }.mergeAll(
            localizationRoutes(),
            gamesRoutes()
        )
    }.start(wait = true)
}

fun <TSubject : Any, TContext : Any> Pipeline<TSubject, TContext>.mergeAll(
    vararg pipelines: Pipeline<TSubject, TContext>
) {
    pipelines.forEach(this::merge)
}
