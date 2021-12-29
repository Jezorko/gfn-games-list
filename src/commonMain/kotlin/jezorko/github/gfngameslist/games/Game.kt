package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.shared.EnumWithReadableName
import jezorko.github.gfngameslist.shared.fromReadableName
import kotlinx.serialization.Serializable

enum class GameStatus {
    AVAILABLE, MAINTENANCE, PATCHING, UNKNOWN
}

enum class Store(override val readableName: String) : EnumWithReadableName {
    STEAM("Steam"),
    EPIC("Epic Games"),
    ORIGIN("Origin"),
    UBISOFT_CONNECT("Ubisoft Connect"),
    GOG("GOG"),
    UNKNOWN("Unknown")
}

fun storeFromReadableName(readableName: String?) =
    fromReadableName(Store.values(), readableName, Store.STEAM)

val unsupportedStores = setOf(Store.UNKNOWN)
val validStores = Store.values().toSet() - unsupportedStores

@Serializable
data class Game(
    val id: Long,
    val title: String,
    val store: Store,
    val imageUrl: String,
    val registeredAt: Long,
    val updatedAt: Long,
    val status: GameStatus,
    val publisher: String,
    val storeUrl: String,
    val genres: Set<String>
)