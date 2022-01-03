package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.shared.EnumWithReadableName
import jezorko.github.gfngameslist.shared.fromReadableName
import kotlinx.serialization.Serializable

enum class Region(override val readableName: String) : EnumWithReadableName {
    US_CENTRAL("US Central"),
    US_EAST("US East"),
    US_MIDWEST("US Midwest"),
    US_NORTHEAST("US Northeast"),
    US_NORTHWEST("US Northwest"),
    US_SOUTH("US South"),
    US_SOUTH_2("US South 2"),
    US_SOUTHWEST("US Southwest"),
    US_WEST_2("US West 2"),
    EU_CENTRAL_2("EU Central 2"),
    EU_CENTRAL_3("EU Central 3"),
    EU_CENTRAL_4("EU Central 4"),
    EU_WEST("EU West"),
    EU_NORTHWEST("EU Northwest"),
    EU_SOUTHEAST("EU Southeast")
}

enum class GameControls {
    KEYBOARD, MOUSE, GAMEPAD_PARTIAL, GAMEPAD,

    UNKNOWN
}

enum class GameGenre {
    ACTION, ADVENTURE, INDIE, ROLE_PLAYING, FIRST_PERSON_SHOOTER, FREE_TO_PLAY, MULTIPLAYER_ONLINE_BATTLE_ARENA,
    STRATEGY, MASSIVELY_MULTIPLAYER_ONLINE, SIMULATION, CASUAL, RACING, PUZZLE, TECH_DEMO, ARCADE, SPORTS, PLATFORMER,
    FAMILY,

    UNKNOWN
}

enum class GameStatus {
    AVAILABLE, SERVER_MAINTENANCE, PATCHING,

    UNKNOWN
}

enum class GameStore(override val readableName: String) : EnumWithReadableName {
    STEAM("Steam"),
    EPIC("Epic Games"),
    ORIGIN("Origin"),
    UPLAY("Ubisoft Connect"),
    GOG("GOG"),
    NVIDIA("Nvidia"),
    NONE("None"),

    UNKNOWN("Unknown")
}

fun storeFromReadableName(readableName: String?) =
    fromReadableName(GameStore.values(), readableName, GameStore.STEAM)

val unsupportedStores = setOf(GameStore.NONE, GameStore.UNKNOWN)
val validStores = GameStore.values().toSet() - unsupportedStores

@Serializable
data class Game(
    val id: String,
    val title: String,
    val stores: Set<GameStore>,
    val imageUrl: String,
    val registeredAt: Long,
    val updatedAt: Long,
    val status: GameStatus,
    val publisher: String,
    val storeUrl: String,
    val genres: Set<GameGenre>,
    val regions: Set<Region>
)