import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import service.AsyncFileDataService
import service.BlockingFileDataService
import service.JDBCDatabaseService
import service.ReactiveDatabaseService
import visitor.ConcurrentVisitor
import visitor.SyncVisitor
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainVerticle : CoroutineVerticle() {

    override suspend fun start() {
//        addData()
        val timesScope = listOf(
                1, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000
        )
        vertx.deployVerticleAwait(DataServiceVerticle(BlockingFileDataService(), "blocking-file"))
        vertx.deployVerticleAwait(DataServiceVerticle(JDBCDatabaseService(), "jdbc"))

        vertx.deployVerticleAwait(DataServiceVerticle(AsyncFileDataService(), "async-file"))
        vertx.deployVerticle(DataServiceVerticle(ReactiveDatabaseService(), "reactive-database"))

        val syncBlockingFileVisitor = SyncVisitor(vertx, "blocking-file")
        val syncJDBCVisitor = SyncVisitor(vertx, "jdbc")
        val syncAsyncFileVisitor = SyncVisitor(vertx, "async-file")
        val syncReactiveVisitor = SyncVisitor(vertx, "reactive-database")

        syncJDBCVisitor.testRequest(1)
        syncReactiveVisitor.testRequest(1)

        timesScope.take(5).forEach {
            val costTime = syncBlockingFileVisitor.testRequest(it)
            println("sync-${syncBlockingFileVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }
        timesScope.take(5).forEach {
            val costTime = syncAsyncFileVisitor.testRequest(it)
            println("sync-${syncAsyncFileVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }
        timesScope.take(10).forEach {
            val costTime = syncJDBCVisitor.testRequest(it)
            println("sync-${syncJDBCVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }
        timesScope.take(10).forEach {
            val costTime = syncReactiveVisitor.testRequest(it)
            println("sync-${syncReactiveVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }


        val concurrentBlockingFileVisitor = ConcurrentVisitor(vertx, "blocking-file")
        val concurrentJDBCVisitor = ConcurrentVisitor(vertx, "jdbc")
        val concurrentFileVisitor = ConcurrentVisitor(vertx, "async-file")
        val concurrentDatabaseVisitor = ConcurrentVisitor(vertx, "reactive-database")

        timesScope.take(5).drop(3).forEach {
            val costTime = concurrentBlockingFileVisitor.testRequest(it / 50, 50)
            println("concurrent-${concurrentBlockingFileVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }
        timesScope.take(5).drop(3).forEach {
            val costTime = concurrentFileVisitor.testRequest(it / 50, 50)
            println("concurrent-${concurrentFileVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }
        timesScope.take(10).drop(3).forEach {
            val costTime = concurrentJDBCVisitor.testRequest(it / 50, 50)
            println("concurrent-${concurrentJDBCVisitor.prefix}: $it : ${costTime.toMillis()}ms")
        }
        timesScope.take(10).drop(3).forEach {
            val costTime = concurrentDatabaseVisitor.testRequest(it / 50, 50)
            println("concurrent-${concurrentDatabaseVisitor.prefix}: $it : ${costTime.toMillis()}ms")
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