package com.cryptodanilo.project.server.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import java.net.URI
import javax.sql.DataSource

private const val MISSING_CREDENTIALS_MESSAGE = "DATABASE_URL must include credentials (postgresql://user:password@host:port/db)"
private const val JDBC_URL_PREFIX = "jdbc:postgresql://"
private const val POSTGRES_DRIVER_CLASS_NAME = "org.postgresql.Driver"
private const val MAXIMUM_POOL_SIZE = 5
private const val PREPARE_THRESHOLD_PROPERTY = "prepareThreshold"
private const val PREPARE_THRESHOLD_DISABLED = "0"
private const val USER_INFO_DELIMITER = ":"
private const val USER_INFO_SPLIT_LIMIT = 2

/**
 * Connects via Supabase's connection pooler (transaction-mode PgBouncer, the
 * `DATABASE_URL` provided to this server should point at its port, 6543 --
 * not the direct-connection port), per the backend data platform's own
 * convention.
 *
 * `prepareThreshold=0` is required, not optional: PgBouncer's transaction
 * pooling mode can route different transactions on the same logical
 * connection to different backend Postgres processes. The JDBC driver's
 * default (prepareThreshold=5) switches repeated queries to a server-side
 * PREPARE after a few uses; a statement prepared on one backend doesn't
 * exist on another, producing failures. This is the exact same class of bug
 * already found and fixed in the Python recurring jobs (psycopg3's
 * `prepare_threshold=None`) -- same root cause, same fix, different driver.
 */
object DatabaseFactory {
    fun init(databaseUrl: String): DataSource {
        val dataSource = HikariDataSource(hikariConfig(databaseUrl))
        Database.connect(dataSource)
        return dataSource
    }

    private fun hikariConfig(databaseUrl: String): HikariConfig {
        val uri = URI(databaseUrl)
        val userInfo =
            uri.userInfo
                ?: error(MISSING_CREDENTIALS_MESSAGE)
        val (username, password) = userInfo.split(USER_INFO_DELIMITER, limit = USER_INFO_SPLIT_LIMIT)
        val query = uri.query?.let { "?$it" }.orEmpty()
        val jdbcUrl = "$JDBC_URL_PREFIX${uri.host}:${uri.port}${uri.path}$query"

        return HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            driverClassName = POSTGRES_DRIVER_CLASS_NAME
            maximumPoolSize = MAXIMUM_POOL_SIZE
            addDataSourceProperty(PREPARE_THRESHOLD_PROPERTY, PREPARE_THRESHOLD_DISABLED)
            validate()
        }
    }
}
