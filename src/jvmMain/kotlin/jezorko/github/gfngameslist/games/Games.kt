package jezorko.github.gfngameslist.games

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

internal object Games : UUIDTable() {

    val title = varchar("title", 255)
    val launcher = varchar("launcher", 255)
    val launcherGameId = varchar("launcher_game_id", 255)
    val imageUrl = varchar("image_url", 255)
    val registeredAt = long("registered_at").default(-1)
    val updatedAt = long("updated_at")

}

internal class GameEntity(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<GameEntity>(Games)

    var title by Games.title
    var launcher by Games.launcher
    var launcherGameId by Games.launcherGameId
    var imageUrl by Games.imageUrl
    var registeredAt by Games.registeredAt
    var updatedAt by Games.updatedAt

}