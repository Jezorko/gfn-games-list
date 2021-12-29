package localization

import jezorko.github.gfngameslist.shared.EnumWithReadableName
import jezorko.github.gfngameslist.shared.fromReadableName

enum class Language(override val readableName: String, val tag: String) : EnumWithReadableName {
    ENGLISH("English", "en-US"),
    POLISH("Polski", "pl-PL")
}

fun languageFromReadableName(readableName: String?) =
    fromReadableName(Language.values(), readableName, Language.ENGLISH)