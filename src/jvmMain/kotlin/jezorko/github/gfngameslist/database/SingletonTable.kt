package jezorko.github.gfngameslist.database

import jezorko.github.gfngameslist.database.Database.doInTransaction
import jezorko.github.gfngameslist.database.Database.insertIfNotExists
import jezorko.github.gfngameslist.database.Database.insertOrUpdate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder

private const val ONLY_ID = 0

open class SingletonTable : Table() {

    private val id = integer("id").default(ONLY_ID)
    override val primaryKey = PrimaryKey(id)

    private val selectQuery: SqlExpressionBuilder.() -> Op<Boolean> = { id eq ONLY_ID }

    fun <T : SingletonTable> T.initialize(insert: T.(UpdateBuilder<Any>) -> Unit) = doInTransaction {
        insertIfNotExists(selectQuery) {
            it[id] = ONLY_ID
            this.insert(it)
        }
    }

    fun <T : SingletonTable> T.getValue(): ResultRow? = select(selectQuery).firstOrNull()

    fun <T : SingletonTable> T.setValue(insert: T.(UpdateBuilder<Any>) -> Unit) = doInTransaction {
        insertOrUpdate(selectQuery) { updatedValue ->
            updatedValue[id] = ONLY_ID
            this.insert(updatedValue)
        }
    }

}