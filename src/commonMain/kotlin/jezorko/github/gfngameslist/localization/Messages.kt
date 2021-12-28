package jezorko.github.gfngameslist.localization

import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty1

@Serializable
data class Messages(
    val supportedGamesCount: String,
    val gameImage: String,
    val gameTitle: String,
    val availableOnPlatform: String
)

operator fun Messages?.get(prop: KProperty1<Messages, String>): String {
    return if (this == null) "?"
    else prop.get(this)
}