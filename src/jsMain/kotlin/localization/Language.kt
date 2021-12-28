package localization

enum class Language(val readableName: String, val tag: String) {
    ENGLISH("English", "en-US"),
    POLISH("Polski", "pl-PL")
}

fun languageFromReadableName(readableName: String?) =
    Language.values().firstOrNull { it.readableName == readableName } ?: Language.ENGLISH