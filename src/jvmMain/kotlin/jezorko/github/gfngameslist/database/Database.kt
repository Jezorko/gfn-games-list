package jezorko.github.gfngameslist.database

import jezorko.github.gfngameslist.games.Games
import jezorko.github.gfngameslist.games.LatestUpdates
import jezorko.github.gfngameslist.games.LatestUpdates.getValue
import jezorko.github.gfngameslist.games.LatestUpdates.initialize
import jezorko.github.gfngameslist.games.LatestUpdates.setValue
import jezorko.github.gfngameslist.shared.Configuration
import jezorko.github.gfngameslist.shared.Environment
import mu.KotlinLogging.logger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.transaction

interface DatabaseConfiguration {
    val jdbcUrl: String
    val username: String
    val password: String
}

object Database {

    private object DatabaseVersions : SingletonTable() {
        val version = long("version")
    }

    private const val INITIAL_VERSION = 0L

    /**
     * Update when breaking migrations are applied
     */
    private const val currentVersion = 2L

    private val log = logger {}
    private val sqlLogger = object : SqlLogger {
        override fun log(context: StatementContext, transaction: Transaction) {
            log.info { "executing SQL: ${context.expandArgs(transaction)}" }
        }
    }
    private val configuration = when (Configuration.ENVIRONMENT.value) {
        Environment.HEROKU -> PostgresHeroku()
        Environment.LOCAL -> PostgresLocal()
    }

    private val registeredTables = setOf(Games, LatestUpdates)

    init {
        doInTransaction {
            SchemaUtils.create(DatabaseVersions)
            DatabaseVersions.initialize { it[version] = INITIAL_VERSION }
            val databaseVersion = DatabaseVersions.getValue()!![DatabaseVersions.version]
            if (databaseVersion != currentVersion) {
                log.info { "database version $databaseVersion older than current $currentVersion, wiping database" }
                recreateTables()
                DatabaseVersions.setValue { it[version] = currentVersion }
            } else {
                registeredTables.forEach(SchemaUtils::create)
            }
        }
    }

    fun recreateTables() = doInTransaction {
        log.info { "recreating tables" }
        addLogger(sqlLogger)
        registeredTables.forEach { table ->
            log.info { "recreating ${table.tableName}" }
            exec("DROP TABLE IF EXISTS ${table.tableName}")
            SchemaUtils.create(table)
        }
        log.info { "tables recreated" }
    }

    fun <T> doInTransaction(operation: Transaction.() -> T) = getConnection().run {
        transaction {
            if (Configuration.LOG_SQL_QUERIES.value) addLogger(sqlLogger)
            operation(this)
        }
    }

    private fun getConnection() = Database.connect(
        url = configuration.jdbcUrl,
        user = configuration.username,
        password = configuration.password
    )

    /**
     * Same as the other variant, but applicable when the update does not depend on existing values.
     */
    fun <T : Table> T.insertOrUpdate(
        where: SqlExpressionBuilder.() -> Op<Boolean>,
        update: T.(UpdateBuilder<Any>) -> Unit
    ) {
        insertOrUpdate(where) { _, updateBuilder -> this.update(updateBuilder) }
    }

    /**
     * First fetches the existing row, if the row exists it is updated, if it does not it is inserted.
     */
    fun <T : Table> T.insertOrUpdate(
        where: SqlExpressionBuilder.() -> Op<Boolean>,
        update: T.(existingValue: ResultRow?, UpdateBuilder<Any>) -> Unit
    ) {
        val existingValue = select(where).limit(1).firstOrNull()
        if (existingValue != null) {
            update(where) { this.update(existingValue, it) }
        } else {
            insert { this.update(null, it) }
        }
    }

    fun <T : Table> T.insertIfNotExists(
        where: SqlExpressionBuilder.() -> Op<Boolean>,
        insert: T.(UpdateBuilder<Any>) -> Unit
    ): ResultRow {
        return select(where).limit(1).firstOrNull() ?: insert { this.insert(it) }.resultedValues!!.first()
    }

}