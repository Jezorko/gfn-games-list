package jezorko.github.gfngameslist.games

import kotlinx.serialization.Serializable

enum class Launcher {
    STEAM, EPIC, ORIGIN, NONE, UPLAY, UNKNOWN;
}

val unsupportedLaunchers = setOf(Launcher.UNKNOWN, Launcher.NONE)
val validLaunchers = Launcher.values().toSet() - unsupportedLaunchers

@Serializable
data class Game(
    val title: String,
    val launcher: Launcher,
    val launcherGameId: String,
    val imageUrl: String,
    val registeredAt: Long,
    val updatedAt: Long
)