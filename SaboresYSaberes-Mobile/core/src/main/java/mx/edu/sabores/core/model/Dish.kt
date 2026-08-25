package mx.edu.sabores.core.model

data class Dish(
    val id: String,
    val name: String,
    val region: Region,
    val shortDescription: String,
    val ingredients: List<String>,
    val preparation: List<String>,
    val culturalHistory: String,
    val imageKey: String,
    val videoUrl: String? = null
)

enum class Region(val displayName: String) {
    OAXACA("Oaxaca"), YUCATAN("Yucatán"), PUEBLA("Puebla"), JALISCO("Jalisco"), MICHOACAN("Michoacán")
}
