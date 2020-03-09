package service

import DataService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.ResultSet
import io.vertx.kotlin.core.json.jsonArrayOf
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.ext.sql.queryAwait
import io.vertx.kotlin.ext.sql.queryWithParamsAwait
import io.vertx.kotlin.ext.sql.updateWithParamsAwait
import java.time.Instant

class JDBCDatabaseService : DataService {

    private lateinit var client: JDBCClient

    override suspend fun init() {
        client = JDBCClient.createShared(Vertx.currentContext().owner(), jsonObjectOf(
                "url" to "jdbc:postgresql://localhost:5432/project1",
                "driver_class" to "org.postgresql.Driver",
                "user" to "checker",
                "password" to "123456"
        ))
    }


    override suspend fun insert(cityData: JsonObject) {
        client.updateWithParamsAwait("""
            INSERT INTO city (name, englishName, zipCode, confirmedCount, suspectedCount, curedCount, deadCount, updateTime)
            VALUES (?,?,?,?,?,?,?,?)
        """.trimIndent(), jsonArrayOf(
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
                                  ON c1.name = c2.name and c1.updateTime = c2.last_time;
                """.trimIndent()
        ).toJsonArray()

    }

    override suspend fun cityData(name: String): JsonArray {

        return client.queryWithParamsAwait(
                """
                    SELECT * FROM city
                    WHERE name = ?                   
                """.trimIndent(),
                jsonArrayOf(
                        name
                )
        ).toJsonArray()
    }

    fun ResultSet.toJsonArray(): JsonArray = JsonArray(
            this.results.map {
                jsonObjectOf(
                        "id" to it.getInteger(0),
                        "name" to it.getString(1),
                        "englishName" to it.getString(2),
                        "zipCode" to it.getString(3),
                        "confirmedCount" to it.getInteger(4),
                        "suspectedCount" to it.getInteger(5),
                        "curedCount" to it.getInteger(6),
                        "deadCount" to it.getInteger(7),
                        "updateTime" to it.getInstant(8)
                )
            }
    )

}