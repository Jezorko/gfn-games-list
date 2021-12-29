package jezorko.github.gfngameslist.games

import kotlinx.serialization.Serializable

enum class GameStatus {
    AVAILABLE, MAINTENANCE, PATCHING, UNKNOWN
}

enum class Store {
    STEAM, EPIC, ORIGIN, UBISOFT_CONNECT, GOG, UNKNOWN;
}

val unsupportedStores = setOf(Store.UNKNOWN)
val validStores = Store.values().toSet() - unsupportedStores

@Serializable
data class Game(
    val id: Long,
    val title: String,
    val store: Store,
    val launcherGameId: Long,
    val imageUrl: String,
    val registeredAt: Long,
    val updatedAt: Long,
    val status: GameStatus,
    val publisher: String,
    val storeUrl: String,
    val genres: Set<String>
)