package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.games.LatestUpdates.id
import jezorko.github.gfngameslist.games.LatestUpdates.timestamp
import jezorko.github.gfngameslist.shared.Configuration
import org.jetbrains.exposed.sql.*
import java.lang.System.currentTimeMillis

private const val ONLY_ID = 0


private object LatestUpdates : Table() {

    val id = integer("id").default(ONLY_ID)
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)

}

object LatestUpdatesRepository {

    init {
        doInTransaction {
            SchemaUtils.create(LatestUpdates)

            if (LatestUpdates.select { id eq ONLY_ID }.firstOrNull() == null) {
                LatestUpdates.insert {
                    it[id] = ONLY_ID
                    it[timestamp] = 0
                }
            }
        }
    }

    fun shouldUpdate() = doInTransaction {
        val timestampNow = currentTimeMillis()
        val updateFrequencyMilliseconds = Configuration.UPDATE_FREQUENCY_MILLISECONDS.value
        val latestUpdateTimestamp = LatestUpdates.select { id eq ONLY_ID }.first()[timestamp]
        timestampNow - latestUpdateTimestamp > updateFrequencyMilliseconds
    }

    fun registerUpdateComplete() = doInTransaction {
        LatestUpdates.update {
            it[id] = ONLY_ID
            it[timestamp] = currentTimeMillis()
        }
    }

}