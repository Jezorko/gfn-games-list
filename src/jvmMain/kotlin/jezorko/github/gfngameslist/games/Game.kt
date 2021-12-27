package jezorko.github.gfngameslist.games

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class Launcher {
    STEAM, EPIC, ORIGIN, NONE, UPLAY,

    @JsonEnumDefaultValue
    UNKNOWN;
}

data class Game(
    val title: String,
    val launcher: Launcher,
    val launcherGameId: String,
    val imageUrl: String
)