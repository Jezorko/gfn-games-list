package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.database.Database.insertOrUpdate
import jezorko.github.gfngameslist.database.Database.optionalAnd
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.upperCase

internal object GamesRepository {

    init {
        doInTransaction { SchemaUtils.create(Games) }
    }

    internal fun putGame(game: Game) = doInTransaction {
        Games.insertOrUpdate({
            Games.id eq game.id
        }) { existingValue, updatedValue ->
            val registrationTime = existingValue?.get(registeredAt) ?: -1
            updatedValue[launcherGameId] = game.launcherGameId
            updatedValue[launcher] = game.store.toString()
            updatedValue[title] = game.title
            updatedValue[imageUrl] = game.imageUrl
            updatedValue[registeredAt] = if (registrationTime == -1L) game.registeredAt else registrationTime
            updatedValue[updatedAt] = game.updatedAt
        }
    }

    internal fun countSupportedGames() = doInTransaction {
        Games.select { Games.launcher notInList unsupportedStores.map(Store::name) }
            .distinctBy { it[Games.title] }
            .count()
    }

    internal fun getGames(limit: Int, page: Int, titlePart: String?, store: Store?) = doInTransaction {
        Games.select {
            (Games.launcher notInList unsupportedStores.map(Store::name))
                .optionalAnd(titlePart) { Games.title.upperCase() like "%${it.uppercase()}%" }
                .optionalAnd(store) { Games.launcher eq it.name }
        }.orderBy(Games.title).orderBy(Games.launcherGameId).limit(
            limit,
            page.toLong() * limit
        ).map {
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
                updatedAt = it[Games.updatedAt]
            )
        }
    }

}