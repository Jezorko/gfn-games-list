package jezorko.github.gfngameslist.nvidia

import com.fasterxml.jackson.annotation.JsonProperty
import jezorko.github.gfngameslist.games.*
import jezorko.github.gfngameslist.shared.httpClient
import jezorko.github.gfngameslist.shared.parseJson
import mu.KotlinLogging.logger
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.ZonedDateTime


enum class VpcId(val region: Region) {
    NP_DAL_01(Region.US_CENTRAL),
    NP_DAL_02(Region.US_CENTRAL),
    NP_ASH_02(Region.US_EAST),
    NP_ASH_03(Region.US_EAST),
    NP_CHI_01(Region.US_MIDWEST),
    NP_CHI_02(Region.US_MIDWEST),
    NP_CHI_03(Region.US_MIDWEST),
    NP_NWK_01(Region.US_NORTHEAST),
    NP_NWK_02(Region.US_NORTHEAST),
    NP_SEA_01(Region.US_NORTHWEST),
    NP_SEA_02(Region.US_NORTHWEST),
    NP_ATL_01(Region.US_SOUTH),
    NP_ATL_02(Region.US_SOUTH),
    NP_MIA_01(Region.US_SOUTH_2),
    NP_MIA_02(Region.US_SOUTH_2),
    NP_LAX_01(Region.US_SOUTHWEST),
    NP_LAX_02(Region.US_SOUTHWEST),
    NP_SJC6_01(Region.US_WEST_2),
    NP_SJC6_02(Region.US_WEST_2),
    NP_SJC6_03(Region.US_WEST_2),
    NP_AMS_01(Region.EU_CENTRAL_2),
    NP_AMS_02(Region.EU_CENTRAL_2),
    NP_AMS_03(Region.EU_CENTRAL_2),
    NP_AMS_04(Region.EU_CENTRAL_2),
    NP_FRK_02(Region.EU_CENTRAL_3),
    NP_FRK_03(Region.EU_CENTRAL_3),
    NP_FRK_04(Region.EU_CENTRAL_3),
    NP_PAR_01(Region.EU_CENTRAL_4),
    NP_PAR_02(Region.EU_CENTRAL_4),
    NP_PAR_03(Region.EU_CENTRAL_4),
    NP_LON_02(Region.EU_WEST),
    NP_LON_03(Region.EU_WEST),
    NP_STH_01(Region.EU_NORTHWEST),
    NP_STH_02(Region.EU_NORTHWEST),
    NP_SOF_01(Region.EU_SOUTHEAST)
}

data class SupportedGameImages(
    @JsonProperty("GAME_BOX_ART")
    val gameBoxArt: String,
    @JsonProperty("HERO_IMAGE")
    val heroImage: String,
    @JsonProperty("KEY_ART")
    val keyArt: String,
    @JsonProperty("TV_BANNER")
    val tvBanner: String
)

data class SupportedGameVariantGeForceNowInfo(
    val releaseDate: ZonedDateTime?,
    val status: GameStatus,
    val features: Set<String>
)

data class SupportedGameVariant(
    val appStore: GameStore,
    val id: String,
    val supportedControls: Set<GameControls>,
    val gfn: SupportedGameVariantGeForceNowInfo
)

data class SupportedGameComputedValues(val allKeywords: Set<String>)

data class SupportedGame(
    val id: String,
    val title: String,
    val publisherName: String,
    val genres: Set<GameGenre>,
    val maxLocalPlayers: Int,
    val images: SupportedGameImages?,
    val imageUrl: String = images?.gameBoxArt ?: "",
    val variants: List<SupportedGameVariant>,
    val computedValues: SupportedGameComputedValues,
    val supportedRegions: Set<Region> = emptySet()
)

data class GetSupportedGamesResponseDataPageInfo(val hasNextPage: Boolean, val endCursor: String)

data class GetSupportedGamesResponseDataApps(
    val numberReturned: Int,
    val pageInfo: GetSupportedGamesResponseDataPageInfo,
    val items: List<SupportedGame>
)

data class GetSupportedGamesResponseData(val apps: GetSupportedGamesResponseDataApps)

data class GetSupportedGamesResponse(val data: GetSupportedGamesResponseData)

object NVidiaApiClient {

    private val log = logger {}

    fun fetchSupportedGamesList() = VpcId.values().map { vpcId ->
        log.info { "fetching games from $vpcId" }
        vpcId to httpClient.sendAsync(
            HttpRequest.newBuilder()
                .uri(URI.create(getGamesUrl(vpcId)))
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }.asSequence()
        .map { it.first to it.second.get() }
        .flatMap { vpcIdToResponse ->
            if (vpcIdToResponse.second.statusCode() == 200) {
                log.info { "fetch from ${vpcIdToResponse.first} successful" }
                try {
                    parseJson(
                        vpcIdToResponse.second.body(),
                        GetSupportedGamesResponse::class
                    ).data.apps.items.asSequence().map { game ->
                        game.copy(supportedRegions = setOf(vpcIdToResponse.first.region))
                    }
                } catch (jsonParseException: Exception) {
                    log.error(jsonParseException) { "failed to parse response body: ${vpcIdToResponse.second.body()}" }
                    emptySequence()
                }
            } else {
                log.warn { "failed to fetch the list of games from ${vpcIdToResponse.first}, status: ${vpcIdToResponse.second.statusCode()}" }
                emptySequence()
            }
        }
        .groupingBy(SupportedGame::title)
        .reduce { _, gamesSoFar, game ->
            game.copy(supportedRegions = gamesSoFar.supportedRegions + game.supportedRegions)
        }.values
        .toList()
        .also { gamesList ->
            if (gamesList.isEmpty()) {
                throw IllegalStateException("failed to fetch game data")
            }
        }

    private fun getGamesUrl(vpcId: VpcId) =
        "https://public.games.geforce.com/graphql?requestType=apps&query=" +
                URLEncoder.encode(
                    "{ apps(vpcId:\"${vpcId.name.replace('_', '-')}\",language: \"en_US\",) {" +
                            "numberReturned pageInfo{ hasNextPage endCursor" +
                            "} items { id" +
                            " genres, publisherName images { GAME_BOX_ART HERO_IMAGE KEY_ART TV_BANNER }" +
                            " computedValues { allKeywords }" +
                            " maxLocalPlayers title variants" +
                            " { appStore id supportedControls" +
                            " gfn { releaseDate status features { ...feature } } } } } }" +
                            " fragment feature on GfnSubscriptionFeature" +
                            " { __typename ... on GfnSubscriptionFeatureValue { key value } ..." +
                            " on GfnSubscriptionFeatureValueList { key values } }",
                    "utf-8"
                )

}