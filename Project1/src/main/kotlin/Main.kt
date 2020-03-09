import io.vertx.core.Vertx

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle()).setHandler {
        if (it.failed()) {
            it.cause().printStackTrace()
        }
        vertx.close()
    }
}