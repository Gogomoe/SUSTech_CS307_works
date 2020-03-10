package service

import DataService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.pgclient.preparedQueryAwait
import io.vertx.kotlin.pgclient.queryAwait
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.Tuple
import java.time.ZoneId

class ReactiveDatabaseService() : DataService {

    lateinit var client: PgPool

    override suspend fun init() {
        val connectOptions = PgConnectOptions()
                .setPort(5432)
                .setHost("localhost")
                .setUser("checker")
                .setPassword("123456")
                .setDatabase("project1")
        client = PgPool.pool(Vertx.currentContext().owner(),
                connectOptions, PoolOptions().setMaxSize(5))
    }

    override suspend fun insert(cityData: JsonObject) {
        client.preparedQueryAwait("""
            INSERT INTO city (name, englishName, zipCode, confirmedCount, suspectedCount, curedCount, deadCount, updateTime)
            VALUES ($1,$2,$3,$4,$5,$6,$7,$8)
        """.trimIndent(), Tuple.of(
                cityData.getString("name"),
                cityData.getString("englishName"),
                cityData.getString("zipCode"),
                cityData.getInteger("confirmedCount"),
                cityData.getInteger("suspectedCount"),
                cityData.getInteger("curedCount"),
                cityData.getInteger("deadCount"),
                cityData.getInstant("updateTime")
        ))

    }

    override suspend fun lastData(): JsonArray {
        return client.queryAwait(
                """
                    SELECT c1.*
                    FROM city c1
                             JOIN (SELECT name, max(updateTime) last_time
                                   FROM city
                                   GROUP BY name) c2
                                  ON c1.name = c2.name and c1.updateTime = c2.last_time
                """.trimIndent()
        ).toJsonArray()
    }

    override suspend fun cityData(name: String): JsonArray {
        return client.preparedQueryAwait("""
                    SELECT * FROM city
                    WHERE name = $1                   
                """.trimIndent(), Tuple.of(
                name
        )).toJsonArray()
    }

    override suspend fun select(id: Int): JsonObject {
        return client.preparedQueryAwait("""
                    SELECT * FROM city
                    WHERE id = $1
                """.trimIndent(), Tuple.of(
                id
        )).first().let {
            jsonObjectOf(
                    "id" to it.getInteger(0),
                    "name" to it.getString(1),
                    "englishName" to it.getString(2),
                    "zipCode" to it.getString(3),
                    "confirmedCount" to it.getInteger(4),
                    "suspectedCount" to it.getInteger(5),
                    "curedCount" to it.getInteger(6),
                    "deadCount" to it.getInteger(7),
                    "updateTime" to it.getLocalDateTime(8).atZone(ZoneId.systemDefault()).toInstant()
            )
        }
    }

    private fun RowSet<Row>.toJsonArray() = JsonArray(
            this.map {
                jsonObjectOf(
                        "id" to it.getInteger(0),
                        "name" to it.getString(1),
                        "englishName" to it.getString(2),
                        "zipCode" to it.getString(3),
                        "confirmedCount" to it.getInteger(4),
                        "suspectedCount" to it.getInteger(5),
                        "curedCount" to it.getInteger(6),
                        "deadCount" to it.getInteger(7),
                        "updateTime" to it.getLocalDateTime(8).atZone(ZoneId.systemDefault()).toInstant()
                )
            }
    )

}

