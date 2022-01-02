package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.database.Database.insertOrUpdate
import jezorko.github.gfngameslist.shared.deserializeSet
import jezorko.github.gfngameslist.shared.serialize
import mu.KotlinLogging.logger
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import java.util.*

internal object GamesRepository {

    private val log = logger { }

    internal fun findAll() = doInTransaction {
        Games.selectAll().map {
            Game(
                id = it[Games.id].value.toString(),
                title = it[Games.title],
                stores = GameStore::class.deserializeSet(it[Games.stores]),
                imageUrl = it[Games.imageUrl],
                registeredAt = it[Games.registeredAt],
                updatedAt = it[Games.updatedAt],
                status = try {
                    GameStatus.valueOf(it[Games.status])
                } catch (exception: IllegalArgumentException) {
                    GameStatus.UNKNOWN
                },
                publisher = it[Games.publisher],
                storeUrl = it[Games.storeUrl],
                genres = GameGenre::class.deserializeSet(it[Games.genres])
            )
        }
    }

    internal fun updateGames(games: Collection<Game>) {
        removeGamesNotIn(games.map(Game::id).map(UUID::fromString).toSet())
        log.info { "stale games removed" }
        games.forEach(this::putGame)
        log.info { "existing games updated" }
    }

    private fun removeGamesNotIn(ids: Set<UUID>) = doInTransaction { Games.deleteWhere { Games.id notInList ids } }

    private fun putGame(game: Game) = doInTransaction {
        Games.insertOrUpdate({
            Games.id eq UUID.fromString(game.id)
        }) { existingValue, updatedValue ->
            val registrationTime = existingValue?.get(registeredAt) ?: -1
            updatedValue[id] = UUID.fromString(game.id)
            updatedValue[stores] = game.stores.serialize()
            updatedValue[title] = game.title
            updatedValue[imageUrl] = game.imageUrl
            updatedValue[registeredAt] = if (registrationTime == -1L) game.registeredAt else registrationTime
            updatedValue[updatedAt] = game.updatedAt
            updatedValue[status] = game.status.name
            updatedValue[publisher] = game.publisher
            updatedValue[storeUrl] = game.storeUrl
            updatedValue[genres] = game.genres.serialize()
        }
    }

}