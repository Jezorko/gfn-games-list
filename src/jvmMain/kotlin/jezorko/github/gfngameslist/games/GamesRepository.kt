package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.database.Database.getOrUpdate
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and

internal object GamesRepository {

    init {
        doInTransaction { SchemaUtils.create(Games) }
    }

    internal fun putGame(game: Game) = doInTransaction {
        getOrUpdate(
            uniqueQuery = GameEntity.find {
                (Games.launcher eq game.launcher.toString()) and (Games.launcherGameId eq game.launcherGameId)
            },
            createNew = GameEntity::new,
            update = {
                launcherGameId = game.launcherGameId
                launcher = game.launcher.toString()
                title = game.title
                imageUrl = game.imageUrl
            }
        )
    }

    internal fun getGames(limit: Int, titlePart: String?) = doInTransaction {
        GameEntity.find {
            if (titlePart != null) Games.title like "%$titlePart%"
            else Games.id.isNotNull()
        }.limit(limit)
            .map {
                Game(
                    title = it.title,
                    launcher = try {
                        Launcher.valueOf(it.launcher)
                    } catch (exception: IllegalArgumentException) {
                        Launcher.UNKNOWN
                    },
                    launcherGameId = it.launcherGameId,
                    imageUrl = it.imageUrl
                )
            }
    }

}