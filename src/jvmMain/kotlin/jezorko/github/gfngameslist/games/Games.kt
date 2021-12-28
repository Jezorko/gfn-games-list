package jezorko.github.gfngameslist.games

import org.jetbrains.exposed.dao.id.UUIDTable

internal object Games : UUIDTable() {

    val title = varchar("title", 255)
    val launcher = varchar("launcher", 255)
    val launcherGameId = varchar("launcher_game_id", 255)
    val imageUrl = varchar("image_url", 255)
    val registeredAt = long("registered_at").default(-1)
    val updatedAt = long("updated_at")

}