package api

import jezorko.github.gfngameslist.games.GameGenre
import jezorko.github.gfngameslist.games.GameStore
import jezorko.github.gfngameslist.games.GetGamesResponse
import jezorko.github.gfngameslist.localization.Messages
import jezorko.github.gfngameslist.shared.serialize
import jezorko.github.gfngameslist.versions.VersionInfo
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
        limit: Int,
        page: Int,
        textSearch: String?,
        stores: List<GameStore>?,
        genres: List<GameGenre>?,
        keywords: List<String>?
    ): Promise<GetGamesResponse> {
        val textSearchParam = if (textSearch != null) "&query=${encodeURIComponent(textSearch)}" else ""
        val storeParam = if (stores != null) "&store=${encodeURIComponent(stores.serialize())}" else ""
        val genreParam = if (genres != null) "&genre=${encodeURIComponent(genres.serialize())}" else ""
        val keywordsParam = if (keywords != null) "&keywords=${encodeURIComponent(keywords.joinToString(","))}" else ""
        return window.fetch(
            "/api/games?limit=$limit&page=$page$textSearchParam$storeParam$genreParam$keywordsParam",
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

    fun getVersionInfo(): Promise<VersionInfo> {
        return window.fetch(
            "/api/versions",
            object : RequestInit {
                override var method: String? = "GET"
            }
        ).flatThen { response ->
            if (response.status == 200.toShort()) {
                response.text().then { responseBodyAsText ->
                    json.decodeFromString(responseBodyAsText)
                }
            } else {
                console.error("cannot resolve version information, received HTTP ${response.status}")
                return@flatThen Promise.resolve(VersionInfo("unknown", "unknown"))
            }
        }
    }

}