package jezorko.github.gfngameslist

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import jezorko.github.gfngameslist.database.databaseRoutes
import jezorko.github.gfngameslist.games.gamesRoutes
import jezorko.github.gfngameslist.localization.alternativeLocales
import jezorko.github.gfngameslist.localization.localizationRoutes
import jezorko.github.gfngameslist.localization.primaryLocale
import jezorko.github.gfngameslist.shared.Configuration
import jezorko.github.gfngameslist.versions.versionsRoutes
import kotlinx.html.*
import mu.KotlinLogging.logger

val log = logger { }

const val applicationTitle = "GeForce NOW Supported Games List"
const val applicationDescription = "Searchable list of games supported on GeForce NOW"

fun HTML.index() {
    head {
        title(applicationTitle)
        meta {
            name = "description"
            content = applicationDescription
        }
        meta {
            attributes["property"] = "og:title"
            content = applicationTitle
        }
        meta {
            attributes["property"] = "og:url"
            content = Configuration.APPLICATION_URL.value
        }
        meta {
            attributes["property"] = "og:description"
            content = applicationDescription
        }
        meta {
            attributes["property"] = "og:image"
            content = Configuration.APPLICATION_IMAGE_URL.value
        }
        meta {
            attributes["property"] = "og:type"
            content = "website"
        }
        meta {
            attributes["property"] = "og:locale"
            content = primaryLocale.toString()
        }
        alternativeLocales.forEach {
            meta {
                attributes["property"] = "og:locale:alternate"
                content = it.toString()
            }
        }
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

    embeddedServer(Netty, port = Configuration.PORT.value) {
        routing {
            get("/") { call.respondHtml(HttpStatusCode.OK, HTML::index) }
            get("/favicon.ico") { call.respondRedirect("https://www.nvidia.com/favicon.ico") }
            static("/static") {
                resource("gfn-games-list.js")
                resources("/static")
            }
        }.mergeAll(
            localizationRoutes(),
            gamesRoutes(),
            databaseRoutes(),
            versionsRoutes()
        )
    }.start(wait = true)
}

fun <TSubject : Any, TContext : Any> Pipeline<TSubject, TContext>.mergeAll(
    vararg pipelines: Pipeline<TSubject, TContext>
) {
    pipelines.forEach(this::merge)
}
