package visitor

import Cities
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonArray
import io.vertx.kotlin.core.eventbus.requestAwait
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Math.random
import java.time.Duration
import java.time.Instant

class SyncVisitor(val vertx: Vertx, val prefix: String) {

    private val scope = CoroutineScope(vertx.orCreateContext.dispatcher())

    private val eventBus: EventBus = vertx.eventBus()

    suspend fun requestLastData() {
        eventBus.requestAwait<JsonArray>("$prefix-last-data", null)
    }

    suspend fun requestCityData() {
        eventBus.requestAwait<JsonArray>("$prefix-city-data", jsonObjectOf(
                "cityName" to Cities.random()
        ))
    }

    suspend fun request() {
        if (random() < 0.5) {
            requestLastData()
        } else {
            requestCityData()
        }
    }

    suspend fun testRequest(times: Int): Duration {

        val startTime = Instant.now()

        scope.launch {
            repeat(times) {
                request()
            }
        }.join()

        val endTime = Instant.now()

        return Duration.between(startTime, endTime)
    }

}