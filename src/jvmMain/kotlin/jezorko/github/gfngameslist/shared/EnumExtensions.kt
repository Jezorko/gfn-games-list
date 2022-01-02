package jezorko.github.gfngameslist.shared

import kotlin.reflect.KClass
import kotlin.reflect.full.staticFunctions

fun <T : Enum<T>> KClass<T>.values() = staticFunctions.first { it.name == "values" }.call() as Array<T>
fun <T : Enum<T>> KClass<T>.valueOf(value: String) = staticFunctions.first { it.name == "valueOf" }.call(value) as T

private const val SERIALIZATION_SEPARATOR = ","
fun <T : Enum<T>> Collection<T>.serialize() = toSet().joinToString(SERIALIZATION_SEPARATOR)
fun <T : Enum<T>> KClass<T>.deserializeSet(value: String): Set<T> {
    return if (value.isNotEmpty()) {
        value.split(SERIALIZATION_SEPARATOR).map(this::valueOf).toSet()
    } else {
        emptySet()
    }
}

fun <T : Enum<T>> KClass<T>.maxSerializedLength() = values().toSet().serialize().length