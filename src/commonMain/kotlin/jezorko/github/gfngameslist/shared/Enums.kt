package jezorko.github.gfngameslist.shared

interface EnumWithReadableName {
    val readableName: String
}

fun <T> fromReadableName(values: Array<T>, readableName: String?, default: T)
        where T : Enum<T>, T : EnumWithReadableName = values.firstOrNull { it.readableName == readableName } ?: default