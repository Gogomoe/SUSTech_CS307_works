import java.time.LocalDateTime

data class CityData(
        val id: Int,
        val name: String,
        val englishName: String,
        val zipCode: String,
        val confirmedCount: Int,
        val suspectedCount: Int,
        val curedCount: Int,
        val deadCount: Int,
        val updateTime: LocalDateTime
)