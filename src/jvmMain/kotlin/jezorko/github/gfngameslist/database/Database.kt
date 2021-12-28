package jezorko.github.gfngameslist.database

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

    fun <T> doInTransaction(operation: () -> T) = getConnection().run {
        transaction {
            if (Configuration.LOG_SQL_QUERIES.value) addLogger(sqlLogger)
            operation()
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

}