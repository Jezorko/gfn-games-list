package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.database.Database.insertOrUpdate
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select

internal object GamesRepository {

    init {
        doInTransaction { SchemaUtils.create(Games) }
    }

    internal fun putGame(game: Game) = doInTransaction {
        Games.insertOrUpdate({
            (Games.launcher eq game.launcher.toString()) and (Games.launcherGameId eq game.launcherGameId)
        }) { existingValue, updatedValue ->
            val registrationTime = existingValue?.get(registeredAt) ?: -1
            updatedValue[launcherGameId] = game.launcherGameId
            updatedValue[launcher] = game.launcher.toString()
            updatedValue[title] = game.title
            updatedValue[imageUrl] = game.imageUrl
            updatedValue[registeredAt] = if (registrationTime == -1L) game.registeredAt else registrationTime
            updatedValue[updatedAt] = game.updatedAt
        }
    }

    internal fun getGames(limit: Int, titlePart: String?) = doInTransaction {
        Games.select {
            (Games.launcher notInList listOf(Launcher.NONE, Launcher.UNKNOWN).map(Launcher::name))
                .let {
                    if (titlePart != null) it and (Games.title like "%$titlePart%")
                    else it
                }
        }.limit(limit)
            .map {
                Game(
                    title = it[Games.title],
                    launcher = try {
                        Launcher.valueOf(it[Games.launcher])
                    } catch (exception: IllegalArgumentException) {
                        Launcher.UNKNOWN
                    },
                    launcherGameId = it[Games.launcherGameId],
                    imageUrl = it[Games.imageUrl],
                    registeredAt = it[Games.registeredAt],
                    updatedAt = it[Games.updatedAt]
                )
            }
    }

}