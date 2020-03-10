import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import service.AsyncFileDataService
import service.BlockingFileDataService
import service.JDBCDatabaseService
import service.ReactiveDatabaseService
import visitor.AsyncVisitor
import visitor.SyncVisitor
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainVerticle : CoroutineVerticle() {

    override suspend fun start() {
//        addData()
        val timesScope = listOf(
                1, 10, 20, 50, 100, 200, 500, 1000
        )
        vertx.deployVerticleAwait(DataServiceVerticle(BlockingFileDataService(), "blocking-file"))
        vertx.deployVerticleAwait(DataServiceVerticle(JDBCDatabaseService(), "jdbc"))
        vertx.deployVerticle(DataServiceVerticle(ReactiveDatabaseService(), "reactive-database"))

        val syncBlockingFileVisitor = SyncVisitor(vertx, "blocking-file")
        val syncJDBCVisitor = SyncVisitor(vertx, "jdbc")
        val syncReactiveVisitor = SyncVisitor(vertx, "reactive-database")

        timesScope.take(5).forEach {
            val costTime = syncBlockingFileVisitor.testRequest(it)
            println("sync-${syncBlockingFileVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }
        timesScope.take(8).forEach {
            val costTime = syncJDBCVisitor.testRequest(it)
            println("sync-${syncJDBCVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }
        timesScope.take(8).forEach {
            val costTime = syncReactiveVisitor.testRequest(it)
            println("sync-${syncReactiveVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }

        vertx.deployVerticleAwait(DataServiceVerticle(AsyncFileDataService(), "async-file"))

        val asyncFileVisitor = AsyncVisitor(vertx, "async-file")
        val asyncJDBCVisitor = AsyncVisitor(vertx, "jdbc")
        val asyncDatabaseVisitor = AsyncVisitor(vertx, "reactive-database")

        timesScope.take(5).forEach {
            val costTime = asyncFileVisitor.testRequest(it / 5, 5)
            println("async-${asyncFileVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }
        timesScope.take(8).forEach {
            val costTime = asyncJDBCVisitor.testRequest(it / 5, 5)
            println("async-${asyncJDBCVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }
        timesScope.take(8).forEach {
            val costTime = asyncDatabaseVisitor.testRequest(it / 5, 5)
            println("async-${asyncDatabaseVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }


    }

    private suspend fun addData() {
        val fileService = BlockingFileDataService()
        val databaseService = JDBCDatabaseService()
        fileService.init()
        databaseService.init()

        val dateFormatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                .withZone(ZoneId.systemDefault())

        File("DXYArea.csv").readLines().drop(1).map {
            it.split(',')
        }.forEach {
            val obj = jsonObjectOf(
                    "name" to it[3],
                    "englishName" to it[4],
                    "zipCode" to it[5],
                    "confirmedCount" to it[10].toInt(),
                    "suspectedCount" to it[11].toInt(),
                    "curedCount" to it[12].toInt(),
                    "deadCount" to it[13].toInt(),
                    "updateTime" to Instant.from(dateFormatter.parse(it[14]))
            )
            fileService.insert(obj)
            databaseService.insert(obj)
        }
    }

}