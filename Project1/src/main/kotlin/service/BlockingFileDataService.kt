package service

import DataService
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonArrayOf
import io.vertx.kotlin.core.json.jsonObjectOf
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class BlockingFileDataService : DataService {

    private val file = File("FileData.txt").also {
        if (!it.exists()) it.createNewFile()
    }

    private var id: Int = -1

    private val dateFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())

    override suspend fun init() {
    }

    override suspend fun insert(cityData: JsonObject) {
        if (id == -1) {
            id = (file.readLines().map { it.split(',')[0].toInt() }.max() ?: 0) + 1
        }
        file.appendText(
                """
                    ${id++},
                    ${cityData.getString("name")},
                    ${cityData.getString("englishName")},
                    ${cityData.getString("zipCode")},
                    ${cityData.getInteger("confirmedCount")},
                    ${cityData.getInteger("suspectedCount")},
                    ${cityData.getInteger("curedCount")},
                    ${cityData.getInteger("deadCount")},
                    ${dateFormatter.format(cityData.getInstant("updateTime"))}
                    
                """.trimIndent().replace(",\n", ",")
        )
    }

    override suspend fun lastData(): JsonArray {

        return jsonArrayOf(
                *readFile()
                        .groupingBy { it.getString("name") }
                        .reduce { key, accumulator, element ->
                            if (element.getInstant("updateTime").isAfter(accumulator.getInstant("updateTime"))) element else accumulator
                        }
                        .values.toTypedArray()
        )
    }

    override suspend fun cityData(name: String): JsonArray {

        return jsonArrayOf(
                *readFile()
                        .filter { it.getString("name") == name }
                        .toTypedArray()
        )
    }

    private fun readFile(): List<JsonObject> {
        return file.readLines()
                .map { it.split(',') }
                .map {
                    jsonObjectOf(
                            "id" to it[0].toInt(),
                            "name" to it[1],
                            "englishName" to it[2],
                            "zipCode" to it[3],
                            "confirmedCount" to it[4].toInt(),
                            "suspectedCount" to it[5].toInt(),
                            "curedCount" to it[6].toInt(),
                            "deadCount" to it[7].toInt(),
                            "updateTime" to Instant.from(dateFormatter.parse(it[8]))
                    )
                }
    }

}