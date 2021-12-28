package jezorko.github.gfngameslist.localization

import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty1

@Serializable
data class Messages(
    val supportedGamesCount: String,
    val gameImage: String,
    val gameTitle: String,
    val availableOnPlatform: String,
    val publisher: String,
    val endOfGamesList: String
)

operator fun Messages?.get(prop: KProperty1<Messages, String>, vararg positionalParameters: Any): String {
    return if (this == null) "?"
    else positionalParameters.map(Any::toString)
        .foldIndexed(prop.get(this)) { index, currentValue, parameter ->
            currentValue.replace("\$${index + 1}", parameter)
        }
}