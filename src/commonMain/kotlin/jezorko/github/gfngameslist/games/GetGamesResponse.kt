package jezorko.github.gfngameslist.games

import kotlinx.serialization.Serializable

@Serializable
data class GetGamesResponse(
    val supportedGamesCount: Long,
    val lastUpdatedAt: Long,
    val games: List<Game>
)