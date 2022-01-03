package jezorko.github.gfngameslist.shared

interface EnumWithReadableName {
    val readableName: String
}

fun <T> fromReadableName(values: Array<T>, readableName: String?, default: T)
        where T : Enum<T>, T : EnumWithReadableName = values.firstOrNull { it.readableName == readableName } ?: default

private const val SERIALIZATION_SEPARATOR = ","
fun <T : Enum<T>> Collection<T>.serialize() = toSet().joinToString(SERIALIZATION_SEPARATOR)
fun <T : Enum<T>> deserializeSet(valueOf: (String) -> T, value: String?): Set<T> {
    return value?.split(SERIALIZATION_SEPARATOR)?.map(valueOf)?.toSet() ?: emptySet()
}