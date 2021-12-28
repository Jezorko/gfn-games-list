package jezorko.github.gfngameslist.games

import org.jetbrains.exposed.dao.id.LongIdTable

internal object Games : LongIdTable() {

    val title = varchar("title", 255)
    val launcher = varchar("launcher", 255)
    val launcherGameId = long("launcher_game_id")
    val imageUrl = varchar("image_url", 255)
    val registeredAt = long("registered_at").default(-1)
    val updatedAt = long("updated_at")
    val status = varchar("status", 255)
    val publisher = varchar("publisher", 255)

}