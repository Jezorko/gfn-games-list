package jezorko.github.gfngameslist.games

import kotlinx.serialization.Serializable

enum class Store {
    STEAM, EPIC, ORIGIN, UPLAY, UBISOFT_CONNECT, GOG, NONE, UNKNOWN;
}

val unsupportedStores = setOf(Store.UNKNOWN, Store.NONE)
val validStores = Store.values().toSet() - unsupportedStores

@Serializable
data class Game(
    val id: Long,
    val title: String,
    val store: Store,
    val launcherGameId: Long,
    val imageUrl: String,
    val registeredAt: Long,
    val updatedAt: Long
)