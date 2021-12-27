package jezorko.github.gfngameslist.database

import jezorko.github.gfngameslist.shared.Configuration

class PostgresHeroku : DatabaseConfiguration {

    override val jdbcUrl: String = Configuration.JDBC_DATABASE_URL.value
    override val username = Configuration.JDBC_DATABASE_USERNAME.value
    override val password = Configuration.JDBC_DATABASE_PASSWORD.value

}