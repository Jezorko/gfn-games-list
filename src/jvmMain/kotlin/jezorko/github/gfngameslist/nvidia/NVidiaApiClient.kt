package jezorko.github.gfngameslist.nvidia

import com.fasterxml.jackson.core.type.TypeReference
import jezorko.github.gfngameslist.shared.httpClient
import jezorko.github.gfngameslist.shared.parseJson
import mu.KotlinLogging.logger
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse


data class SupportedGame(
    val id: Long,
    val title: String,
    val sortName: String,
    val isFullyOptimized: Boolean,
    val steamUrl: String,
    val store: String,
    val publisher: String,
    val genres: Set<String>,
    val status: String,
    val imageUrl: String = when (store) {
        "Steam" -> "https://cdn.cloudflare.steamstatic.com/steam/apps/${steamUrl.split('/').last()}/header.jpg"
        else -> ""
    },
    val storeUrl: String = when (store) {
        "Steam" -> steamUrl
        else -> ""
    }
)

object NVidiaApiClient {

    private val log = logger {}

    fun fetchSupportedGamesList(languageTag: String = "en-US"): List<SupportedGame>? {
        val response = httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create(getGamesUrl()))
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )

        return if (response.statusCode() == 200)
            parseJson(response.body(), object : TypeReference<List<SupportedGame>>() {})
        else {
            log.warn {
                "failed to fetch the list of games, status: ${response.statusCode()}, body: ${response.body()}"
            }
            null
        }
    }

    private fun getGamesUrl(languageTag: String = "en-US") =
        "https://static.nvidiagrid.net/supported-public-game-list/locales/gfnpc-${languageTag}.json"

}