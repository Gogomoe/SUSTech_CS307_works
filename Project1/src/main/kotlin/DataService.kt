import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

interface DataService {

    suspend fun insert(cityData: JsonObject)

    suspend fun lastData(): JsonArray

    suspend fun cityData(name: String): JsonArray

    suspend fun init()

}