package jezorko.github.gfngameslist.shared

import kotlin.reflect.KClass
import kotlin.reflect.full.staticFunctions

fun <T : Enum<T>> KClass<T>.values() = staticFunctions.first { it.name == "values" }.call() as Array<T>
fun <T : Enum<T>> KClass<T>.valueOf(value: String) = staticFunctions.first { it.name == "valueOf" }.call(value) as T

fun <T : Enum<T>> KClass<T>.deserializeSet(value: String?) = deserializeSet({ valueOf(it) }, value)
fun <T : Enum<T>> KClass<T>.maxSerializedLength() = values().toSet().serialize().length