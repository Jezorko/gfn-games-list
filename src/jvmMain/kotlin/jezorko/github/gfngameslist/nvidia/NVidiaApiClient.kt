package jezorko.github.gfngameslist.nvidia

import com.fasterxml.jackson.core.type.TypeReference
import jezorko.github.gfngameslist.shared.httpClient
import jezorko.github.gfngameslist.shared.parseJson
import mu.KotlinLogging.logger
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

data class SupportedGame(
    val name: String,
    val launcher: String,
    val launcherGameId: String,
    val imageUrl: String
)

object NVidiaApiClient {

    private val log = logger {}
    private const val baseUrl = "https://gfn.nvidia.com/api/1"

    fun fetchSupportedGamesList(): List<SupportedGame>? {
        val response = httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create("${baseUrl}/products/gfn/games"))
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

}