package service

import DataService
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.file.FileSystem
import io.vertx.core.file.OpenOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.parsetools.RecordParser
import io.vertx.kotlin.core.executeBlockingAwait
import io.vertx.kotlin.core.file.openAwait
import io.vertx.kotlin.core.file.readAwait
import io.vertx.kotlin.core.file.readFileAwait
import io.vertx.kotlin.core.json.jsonArrayOf
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.core.streams.writeAwait
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class AsyncFileDataService : DataService {

    private lateinit var fileSystem: FileSystem

    private var id: Int = -1

    private val dateFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())

    override suspend fun init() {
        fileSystem = Vertx.currentContext().owner().fileSystem()
    }

    override suspend fun insert(cityData: JsonObject) {
        if (id == -1) {
            id = (readFile().map { it.getInteger("id") }.max() ?: 0) + 1
        }

        val data = """
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

        val file = fileSystem.openAwait("FileData.txt", OpenOptions().setAppend(true))
        file.writeAwait(Buffer.buffer(data))

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

    override suspend fun select(id: Int): JsonObject {
        val file = fileSystem.openAwait("FileData.txt", OpenOptions().setRead(true))
        var offset = 0L
        while (true) {
            val buffer = file.readAwait(Buffer.buffer(32 * 1024), 0, offset, 32 * 1024)
            var start = 0
            for (i in 0 until 32 * 1024) {
                if (buffer.getByte(i) == '\n'.toByte()) {
                    val line = buffer.getString(start, i)
                    val it = line.split(',')
                    try {
                        if (it[0].toInt() == id) {
                            file.close()
                            return jsonObjectOf(
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
                    } catch (e: Exception) {
                        println()
                    }

                    start = i + 1
                }
            }
            offset += start.toLong()
        }

    }

    private suspend fun readFile(): List<JsonObject> {
        return fileSystem.readFileAwait("FileData.txt")
                .toString(StandardCharsets.UTF_8)
                .split('\n')
                .filter { it.isNotBlank() }
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