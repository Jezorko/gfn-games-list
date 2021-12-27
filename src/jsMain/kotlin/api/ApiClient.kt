package api

import jezorko.github.gfngameslist.localization.Messages
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import org.w3c.fetch.RequestInit
import shared.flatThen
import shared.json
import kotlin.js.Promise

val localizationMessagesCache = mutableMapOf<String, Promise<Messages?>>()

class ApiClient {

    fun getMessages(languageTag: String) = localizationMessagesCache.getOrPut(languageTag) {
        window.fetch("/api/messages/$languageTag", object : RequestInit {
            override var method: String? = "GET"
        }).flatThen { response ->
            if (response.status == 200.toShort()) {
                response.text().then { responseBodyAsText ->
                    json.decodeFromString<Messages>(responseBodyAsText)
                }
            } else {
                Promise.resolve(null as Messages?)
            }
        }
    }

}