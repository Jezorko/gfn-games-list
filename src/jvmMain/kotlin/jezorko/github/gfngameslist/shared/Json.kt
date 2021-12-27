package jezorko.github.gfngameslist.shared

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlin.reflect.KClass

private val objectMapper = ObjectMapper().registerKotlinModule()

fun <T : Any> parseJson(value: String, clazz: KClass<T>) = objectMapper.readValue(value, clazz.java)

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