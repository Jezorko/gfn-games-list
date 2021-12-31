package jezorko.github.gfngameslist.versions

import io.ktor.application.*
import io.ktor.routing.*
import jezorko.github.gfngameslist.shared.Configuration
import jezorko.github.gfngameslist.shared.respondJson

fun Application.versionsRoutes() = routing {
    get("/api/versions") {
        call.respondJson(provider = {
            VersionInfo(
                repositoryUrl = Configuration.PROJECT_GIT_REPOSITORY.value,
                commitSlug = Configuration.HEROKU_SLUG_COMMIT.value
            )
        })
    }
}