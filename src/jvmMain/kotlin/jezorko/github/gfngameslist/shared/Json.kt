package jezorko.github.gfngameslist.shared

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

val objectMapper = ObjectMapper().registerKotlinModule()

suspend fun ApplicationCall.respondJson(
    status: HttpStatusCode? = null,
    provider: suspend () -> Any
) {
    respondText(
        contentType = ContentType.Application.Json,
        status = status,
        provider = { objectMapper.writeValueAsString(provider()) }
    )
}