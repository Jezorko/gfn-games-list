package jezorko.github.gfngameslist.localization

import jezorko.github.gfngameslist.shared.objectMapper
import java.util.*
import java.util.concurrent.ConcurrentHashMap

val messagesCache = ConcurrentHashMap<Locale, Messages?>()

data class Messages(
    val supportedGamesCount: String
) {
    companion object {
        fun loadForLanguageTag(languageTag: String): Messages? {
            val locale = Locale.forLanguageTag(languageTag)
            return messagesCache.computeIfAbsent(locale) {
                val messages = Messages::class.java.getResource("/localization/messages_$locale.json")
                    ?: return@computeIfAbsent null
                val messagesJsonString = messages.readText()
                objectMapper.readValue(messagesJsonString, Messages::class.java)
            }
        }
    }
}