package jezorko.github.gfngameslist.games

import kotlinx.serialization.Serializable

enum class Launcher {
    STEAM, EPIC, ORIGIN, NONE, UPLAY, UNKNOWN;
}

@Serializable
data class Game(
    val title: String,
    val launcher: Launcher,
    val launcherGameId: String,
    val imageUrl: String,
    val registeredAt: Long,
    val updatedAt: Long
)