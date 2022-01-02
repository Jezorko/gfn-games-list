package jezorko.github.gfngameslist.shared

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import jezorko.github.gfngameslist.games.GameControls
import jezorko.github.gfngameslist.games.GameGenre
import jezorko.github.gfngameslist.games.GameStatus
import jezorko.github.gfngameslist.games.GameStore
import mu.KotlinLogging.logger
import kotlin.reflect.KClass


private class SafeEnumModule : SimpleModule() {

    /**
     * Registry of enums for which deserialization will be performed in a safe manner: when unknown value is found, UNKNOWN is returned.
     */
    private val safeEnums = setOf(GameControls::class, GameGenre::class, GameStatus::class, GameStore::class)

    init {
        safeEnums.forEach { addEnumDeserializer(it) }
    }

    fun <T : Enum<T>> addEnumDeserializer(enumClass: KClass<T>) {
        addDeserializer(enumClass.java, object : StdDeserializer<T>(enumClass.java) {
            private val log = logger { }

            private val allValues = enumClass.values()
            private val unknownValue = allValues.firstOrNull { it.name == "UNKNOWN" }
                ?: throw IllegalStateException("missing 'UNKNOWN' value for enum ${enumClass.simpleName}")
            private val unknownValues = mutableSetOf<String>()

            override fun deserialize(parser: JsonParser?, context: DeserializationContext?): T {
                val nodeValue = when (val node = parser?.codec?.readTree<TreeNode>(parser)) {
                    is TextNode -> node.asText()
                    else -> ""
                }

                val matchingValue = allValues.firstOrNull { it.name == nodeValue }
                return if (matchingValue != null) matchingValue
                else {
                    if (!unknownValues.contains(nodeValue)) {
                        unknownValues.add(nodeValue)
                        log.warn { "found unmatched value '$nodeValue' for enum ${enumClass.simpleName}" }
                    }
                    unknownValue
                }
            }
        })
    }
}

private val objectMapper = ObjectMapper()
    .registerKotlinModule()
    .registerModule(JavaTimeModule())
    .registerModule(SafeEnumModule())
    .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)

fun <T : Any> parseJson(value: String, typeReference: TypeReference<T>): T =
    objectMapper.readValue(value, typeReference)

fun <T : Any> parseJson(value: String, clazz: KClass<T>): T = objectMapper.readValue(value, clazz.java)

suspend fun <T> ApplicationCall.respondJson(status: HttpStatusCode? = null, value: T) = respondJson(status) { value }

suspend fun <T> ApplicationCall.respondJson(
    status: HttpStatusCode? = null,
    provider: () -> T
) {
    respondText(
        contentType = ContentType.Application.Json,
        status = status,
        provider = { objectMapper.writeValueAsString(provider()) }
    )
}