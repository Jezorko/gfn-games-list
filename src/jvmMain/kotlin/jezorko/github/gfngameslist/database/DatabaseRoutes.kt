package jezorko.github.gfngameslist.database

import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.response.*
import io.ktor.routing.*
import jezorko.github.gfngameslist.shared.Configuration

fun Application.databaseRoutes() = routing {
    delete("/api/databases") {
        if (Configuration.ADMIN_TOKEN.value == call.request.headers["X-Auth-Token"]) {
            try {
                Database.recreateTables()
                call.respondText { "tables recreated successfully" }
            } catch (exception: Exception) {
                call.respondText(status = InternalServerError) { "failed to recreate tables" }
            }
        } else {
            call.respondText(status = Unauthorized) { "invalid admin token" }
        }
    }
}