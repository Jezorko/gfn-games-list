package jezorko.github.gfngameslist.localization

import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty1

@Serializable
data class GameStatusMessages(
    val available: String,
    val maintenance: String,
    val patching: String,
    val unknown: String
)

@Serializable
data class Messages(
    val supportedGamesCount: String,
    val gameImage: String,
    val gameTitle: String,
    val availableOnPlatform: String,
    val publisher: String,
    val genres: String,
    val status: String,
    val specificStatus: GameStatusMessages,
    val endOfGamesList: String,
    val searchByTitlePlaceholder: String,
    val searchByPublisherPlaceholder: String,
    val searchByGenresPlaceholder: String
)

operator fun <T> Messages?.get(
    prop: KProperty1<Messages, T>,
    subProp: KProperty1<T, String>,
    vararg positionalParameters: Any
) = toMessage(this?.let(prop)?.let(subProp), positionalParameters)

operator fun Messages?.get(
    prop: KProperty1<Messages, String>,
    vararg positionalParameters: Any
) = toMessage(this?.let(prop), positionalParameters)

private fun toMessage(template: String?, positionalParameters: Array<out Any>): String {
    return if (template == null) "?"
    else positionalParameters.map(Any::toString)
        .foldIndexed(template) { index, currentValue, parameter ->
            currentValue.replace("\$${index + 1}", parameter)
        }
}