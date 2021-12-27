package jezorko.github.gfngameslist.localization

import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.routing.*
import jezorko.github.gfngameslist.shared.ErrorResponse
import jezorko.github.gfngameslist.shared.respondJson

fun Application.localizationRoutes() = routing {
    get("/api/messages/{languageTag}") {
        val languageTag = call.parameters["languageTag"]!!
        val messages = Messages.loadForLanguageTag(languageTag)

        call.respondJson(if (messages != null) OK else NotFound) {
            messages ?: ErrorResponse("language $languageTag is not supported")
        }
    }
}