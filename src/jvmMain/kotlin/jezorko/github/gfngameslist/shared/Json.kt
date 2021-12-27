package jezorko.github.gfngameslist.shared

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlin.reflect.KClass

private val objectMapper = ObjectMapper()
    .registerKotlinModule()
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