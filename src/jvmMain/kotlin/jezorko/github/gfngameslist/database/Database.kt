package jezorko.github.gfngameslist.database

import jezorko.github.gfngameslist.shared.Configuration
import jezorko.github.gfngameslist.shared.Environment
import mu.KotlinLogging.logger
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.statements.StatementContext
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

    fun getConnection() = Database.connect(
        url = configuration.jdbcUrl,
        user = configuration.username,
        password = configuration.password
    )

    fun <T> doInTransaction(operation: () -> T) = getConnection().run {
        transaction {
            if (Configuration.LOG_SQL_QUERIES.value) addLogger(sqlLogger)
            operation()
        }
    }

    fun <ID, T : Entity<ID>> getOrUpdate(
        uniqueQuery: SizedIterable<T>,
        createNew: (T.() -> Unit) -> T,
        update: T.() -> Unit
    ) {
        val existingEntity = uniqueQuery.forUpdate().limit(1).firstOrNull()
        if (existingEntity != null) update(existingEntity)
        else createNew { update.invoke(this) }
    }

}