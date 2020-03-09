import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch

class DataServiceVerticle(val service: DataService, val prefix: String) : CoroutineVerticle() {

    override suspend fun start() {
        service.init()
        val eventBus = vertx.eventBus()
        eventBus.consumer<JsonObject>("$prefix-insert") { message ->
            val request = message.body()
            launch {
                service.insert(request)
                message.reply(null)
            }
        }
        eventBus.consumer<String?>("$prefix-last-data") { message ->
            launch {
                message.reply(service.lastData())
            }
        }
        eventBus.consumer<JsonObject>("$prefix-city-data") { message ->
            val name = message.body().getString("cityName")
            launch {
                message.reply(service.cityData(name))
            }
        }
        eventBus.consumer<JsonObject>("$prefix-select") { message ->
            val id = message.body().getInteger("id")
            launch {
                message.reply(service.select(id))
            }
        }
    }

}