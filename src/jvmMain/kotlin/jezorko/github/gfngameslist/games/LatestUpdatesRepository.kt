package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.database.Database.insertOrUpdate
import jezorko.github.gfngameslist.shared.Configuration
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import java.lang.System.currentTimeMillis

private const val ONLY_ID = 0
private const val INITIAL_TIMESTAMP = 0L


private object LatestUpdates : Table() {

    val id = integer("id").default(ONLY_ID)
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)

}

object LatestUpdatesRepository {

    init {
        doInTransaction {
            SchemaUtils.create(LatestUpdates)
            LatestUpdates.setTimestamp(INITIAL_TIMESTAMP)
        }
    }

    fun shouldUpdate() = doInTransaction {
        val timestampNow = currentTimeMillis()
        val updateFrequencyMilliseconds = Configuration.UPDATE_FREQUENCY_MILLISECONDS.value
        val latestTimestamp = LatestUpdates.getTimestamp()
        timestampNow - latestTimestamp > updateFrequencyMilliseconds
    }

    fun registerUpdateComplete() = doInTransaction {
        LatestUpdates.setTimestamp(currentTimeMillis())
    }

    private fun LatestUpdates.getTimestamp() =
        LatestUpdates.select { id eq ONLY_ID }.firstOrNull()?.get(timestamp) ?: INITIAL_TIMESTAMP

    private fun LatestUpdates.setTimestamp(newTimestamp: Long) {
        LatestUpdates.insertOrUpdate({ id eq ONLY_ID }) { existingValue, updatedValue ->
            updatedValue[id] = ONLY_ID
            updatedValue[timestamp] = existingValue?.get(timestamp) ?: newTimestamp
        }
    }

}