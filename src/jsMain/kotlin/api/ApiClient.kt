package api

import jezorko.github.gfngameslist.games.GetGamesResponse
import jezorko.github.gfngameslist.games.Launcher
import jezorko.github.gfngameslist.localization.Messages
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import localization.Language
import org.w3c.fetch.RequestInit
import shared.flatThen
import shared.json
import kotlin.js.Promise

val localizationMessagesCache = mutableMapOf<Language, Promise<Messages?>>()

external fun encodeURIComponent(str: String): String

object ApiClient {

    fun getMessages(language: Language) = localizationMessagesCache.getOrPut(language) {
        window.fetch("/api/messages/${language.tag}", object : RequestInit {
            override var method: String? = "GET"
        }).flatThen { response ->
            if (response.status == 200.toShort()) {
                response.text().then { responseBodyAsText ->
                    json.decodeFromString(responseBodyAsText)
                }
            } else {
                Promise.resolve(null as Messages?)
            }
        }
    }

    fun getGames(
        limit: Int = 10,
        page: Int = 0,
        titlePart: String? = null,
        launcher: Launcher?
    ): Promise<GetGamesResponse> {
        val titlePartParam = if (titlePart != null) "&title=${encodeURIComponent(titlePart)}" else ""
        val launcherParam = if (launcher != null) "&launcher=${launcher.name}" else ""
        return window.fetch(
            "/api/games?limit=$limit&page=$page$titlePartParam$launcherParam",
            object : RequestInit {
                override var method: String? = "GET"
            }
        ).flatThen { response ->
            if (response.status == 200.toShort()) {
                response.text().then { responseBodyAsText ->
                    json.decodeFromString(responseBodyAsText)
                }
            } else {
                Promise.reject(Error("failed to fetch games: ${response.text()}"))
            }
        }
    }

}