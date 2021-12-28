package jezorko.github.gfngameslist.games

import kotlinx.serialization.Serializable

@Serializable
data class GetGamesResponse(
    val lastUpdatedAt: Long,
    val games: List<Game>
)