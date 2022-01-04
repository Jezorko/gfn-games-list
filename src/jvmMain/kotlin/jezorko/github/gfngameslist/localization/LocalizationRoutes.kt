package jezorko.github.gfngameslist.localization

import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.routing.*
import jezorko.github.gfngameslist.shared.ErrorResponse
import jezorko.github.gfngameslist.shared.respondJson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*
import java.util.concurrent.ConcurrentHashMap

val messagesCache = ConcurrentHashMap<Locale, Messages?>()
val primaryLocale = Locale.forLanguageTag("en-US")
val alternativeLocales = arrayOf(Locale.forLanguageTag("pl-PL"))

private fun loadMessagesForLanguageTag(languageTag: String): Messages? {
    val locale = Locale.forLanguageTag(languageTag)
    return messagesCache.computeIfAbsent(locale) {
        val messages = Messages::class.java.getResource("/localization/messages_$locale.json")
            ?: return@computeIfAbsent null
        val messagesJsonString = messages.readText()
        Json.decodeFromString<Messages>(messagesJsonString)
    }
}

fun Application.localizationRoutes() = routing {
    get("/api/messages/{languageTag}") {
        val languageTag = call.parameters["languageTag"]!!
        val messages = loadMessagesForLanguageTag(languageTag)

        call.respondJson(
            if (messages != null) OK else NotFound,
            messages ?: ErrorResponse("language $languageTag is not supported")
        )
    }
}