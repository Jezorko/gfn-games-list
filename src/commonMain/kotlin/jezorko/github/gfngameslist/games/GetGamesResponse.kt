package jezorko.github.gfngameslist.games

import kotlinx.serialization.Serializable

@Serializable
data class GetGamesResponse(
    val supportedGamesCount: Int,
    val lastUpdatedAt: Long,
    val games: List<Game>
)