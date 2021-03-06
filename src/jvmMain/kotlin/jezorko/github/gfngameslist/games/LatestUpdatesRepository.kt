package jezorko.github.gfngameslist.games

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.database.SingletonTable
import jezorko.github.gfngameslist.games.LatestUpdates.getValue
import jezorko.github.gfngameslist.games.LatestUpdates.initialize
import jezorko.github.gfngameslist.games.LatestUpdates.setValue
import jezorko.github.gfngameslist.games.LatestUpdates.timestamp
import jezorko.github.gfngameslist.shared.Configuration
import java.lang.System.currentTimeMillis

private const val INITIAL_TIMESTAMP = 0L


object LatestUpdates : SingletonTable() {
    val timestamp = long("timestamp")
}

object LatestUpdatesRepository {

    init {
        doInTransaction {
            LatestUpdates.initialize { it[timestamp] = INITIAL_TIMESTAMP }
        }
    }

    fun shouldUpdate() = doInTransaction {
        val timestampNow = currentTimeMillis()
        val updateFrequencyMilliseconds = Configuration.UPDATE_FREQUENCY_MILLISECONDS.value
        val latestTimestamp = lastUpdatedAt()
        timestampNow - latestTimestamp > updateFrequencyMilliseconds
    }

    fun lastUpdatedAt() = LatestUpdates.getValue()?.get(timestamp) ?: INITIAL_TIMESTAMP

    fun registerUpdateComplete(updateTimestamp: Long) = doInTransaction {
        LatestUpdates.setValue { it[timestamp] = updateTimestamp }
    }

}