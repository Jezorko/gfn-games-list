package jezorko.github.gfngameslist.games

data class GetGamesResponse(
    val lastUpdatedAt: Long,
    val games: List<Game>
)