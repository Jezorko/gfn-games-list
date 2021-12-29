package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.database.Database.insertOrUpdate
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

internal object GamesRepository {

    init {
        doInTransaction { SchemaUtils.create(Games) }
    }

    internal fun findAll() = doInTransaction {
        Games.selectAll().map {
            Game(
                id = it[Games.id].value,
                title = it[Games.title],
                store = try {
                    Store.valueOf(it[Games.launcher])
                } catch (exception: IllegalArgumentException) {
                    Store.UNKNOWN
                },
                launcherGameId = it[Games.launcherGameId],
                imageUrl = it[Games.imageUrl],
                registeredAt = it[Games.registeredAt],
                updatedAt = it[Games.updatedAt],
                status = try {
                    GameStatus.valueOf(it[Games.status])
                } catch (exception: IllegalArgumentException) {
                    GameStatus.UNKNOWN
                },
                publisher = it[Games.publisher],
                storeUrl = it[Games.storeUrl]
            )
        }
    }

    internal fun putGame(game: Game) = doInTransaction {
        Games.insertOrUpdate({
            Games.id eq game.id
        }) { existingValue, updatedValue ->
            val registrationTime = existingValue?.get(registeredAt) ?: -1
            updatedValue[id] = game.id
            updatedValue[launcherGameId] = game.launcherGameId
            updatedValue[launcher] = game.store.name
            updatedValue[title] = game.title
            updatedValue[imageUrl] = game.imageUrl
            updatedValue[registeredAt] = if (registrationTime == -1L) game.registeredAt else registrationTime
            updatedValue[updatedAt] = game.updatedAt
            updatedValue[status] = game.status.name
            updatedValue[publisher] = game.publisher
            updatedValue[storeUrl] = game.storeUrl
        }
    }

    internal fun countSupportedGames() = doInTransaction {
        Games.select { Games.launcher notInList unsupportedStores.map(Store::name) }
            .distinctBy { it[Games.title] }
            .count()
    }

}