package mx.edu.sabores.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import mx.edu.sabores.core.model.Dish
import mx.edu.sabores.core.model.Region
import mx.edu.sabores.domain.DishRepository

private val Context.dataStore by preferencesDataStore("sabores_preferences")

class LocalDishRepository(private val context: Context) : DishRepository {
    private val favoriteKey = stringSetPreferencesKey("favorite_ids")
    override fun observeDishes(): Flow<List<Dish>> = flowOf(SampleDishes.all)
    override fun observeFavoriteIds(): Flow<Set<String>> = context.dataStore.data.map { it[favoriteKey] ?: emptySet() }
    override suspend fun findById(id: String): Dish? = SampleDishes.all.find { it.id == id }
    override suspend fun toggleFavorite(id: String) { context.dataStore.edit { p ->
        val current = p[favoriteKey].orEmpty(); p[favoriteKey] = if (id in current) current - id else current + id
    } }
}

object SampleDishes {
    val all = listOf(
        dish("mole-negro", "Mole negro", Region.OAXACA, "Salsa ceremonial de sabor profundo, elaborada con chiles, especias y cacao.", listOf("Chile chilhuacle", "Chocolate", "Especias", "Ajonjolí", "Pollo"), listOf("Tostar los chiles y especias.", "Moler los ingredientes.", "Cocinar lentamente la salsa.", "Servir con pollo y ajonjolí."), "Es uno de los siete moles de Oaxaca y representa la cocina festiva y comunitaria del estado.", "mole", "https://www.youtube.com/results?search_query=mole+negro+oaxaca"),
        dish("tlayuda", "Tlayuda", Region.OAXACA, "Tortilla grande y crujiente con asiento, frijol, quesillo y verduras.", listOf("Tlayuda", "Frijoles", "Quesillo", "Col", "Tasajo"), listOf("Untar asiento y frijoles.", "Añadir quesillo y tasajo.", "Dorar sobre comal.", "Terminar con col y salsa."), "Es una preparación cotidiana de los Valles Centrales y un símbolo popular de Oaxaca.", "tlayuda"),
        dish("cochinita", "Cochinita pibil", Region.YUCATAN, "Cerdo adobado con achiote y cocido lentamente al estilo pibil.", listOf("Carne de cerdo", "Achiote", "Naranja agria", "Hoja de plátano", "Cebolla morada"), listOf("Preparar el recado de achiote.", "Marinar la carne.", "Envolver en hoja de plátano.", "Cocer lentamente y acompañar con cebolla."), "Conserva la técnica maya del pib, horno excavado en tierra, adaptada con ingredientes posteriores.", "cochinita", "https://www.youtube.com/results?search_query=cochinita+pibil+tradicional"),
        dish("sopa-lima", "Sopa de lima", Region.YUCATAN, "Caldo aromático con pavo o pollo, lima y tiras de tortilla.", listOf("Pollo", "Lima yucateca", "Jitomate", "Tortilla", "Especias"), listOf("Preparar el caldo.", "Sofreír jitomate y especias.", "Agregar pollo deshebrado.", "Servir con lima y tortilla."), "Integra cítricos regionales y sabores característicos de la cocina yucateca.", "sopa"),
        dish("mole-poblano", "Mole poblano", Region.PUEBLA, "Salsa espesa de chiles, semillas, especias y chocolate.", listOf("Chiles secos", "Chocolate", "Almendra", "Especias", "Guajolote o pollo"), listOf("Limpiar y freír los chiles.", "Tostar semillas y especias.", "Moler y cocinar la salsa.", "Servir sobre la carne."), "Es un emblema de celebraciones familiares y religiosas de Puebla.", "mole_poblano"),
        dish("chiles-nogada", "Chiles en nogada", Region.PUEBLA, "Chile poblano relleno de picadillo, cubierto con nogada y granada.", listOf("Chile poblano", "Carne y fruta", "Nuez de Castilla", "Granada", "Perejil"), listOf("Asar y limpiar los chiles.", "Preparar el picadillo.", "Rellenar.", "Cubrir con nogada, granada y perejil."), "Platillo de temporada relacionado con la memoria histórica de la Independencia de México.", "chiles", "https://www.youtube.com/results?search_query=chiles+en+nogada+tradicionales"),
        dish("birria", "Birria", Region.JALISCO, "Carne marinada con chiles y especias, cocida hasta quedar suave.", listOf("Carne de chivo o res", "Chile guajillo", "Vinagre", "Especias", "Tortillas"), listOf("Preparar el adobo.", "Marinar la carne.", "Cocer tapada lentamente.", "Servir con caldo, cebolla y limón."), "Originaria de Jalisco, se comparte en fiestas, reuniones y celebraciones familiares.", "birria", "https://www.youtube.com/results?search_query=birria+jalisco+tradicional"),
        dish("torta-ahogada", "Torta ahogada", Region.JALISCO, "Birote relleno de carnitas y bañado en salsa de jitomate y chile.", listOf("Birote salado", "Carnitas", "Jitomate", "Chile de árbol", "Cebolla"), listOf("Abrir y rellenar el birote.", "Preparar la salsa de jitomate.", "Bañar la torta.", "Agregar picante al gusto."), "Es parte de la identidad urbana de Guadalajara y de su cultura de mercados y fondas.", "torta"),
        dish("corundas", "Corundas", Region.MICHOACAN, "Tamales triangulares de maíz, envueltos en hojas de la planta.", listOf("Masa de maíz", "Manteca", "Sal", "Hojas de maíz", "Crema y salsa"), listOf("Batir la masa.", "Formar triángulos en las hojas.", "Cocer al vapor.", "Servir con salsa, crema y queso."), "Preparación de raíz purépecha, presente en celebraciones y cocinas familiares michoacanas.", "corundas"),
        dish("sopa-tarasca", "Sopa tarasca", Region.MICHOACAN, "Sopa cremosa de frijol con jitomate, chile y tortilla crujiente.", listOf("Frijol", "Jitomate", "Chile pasilla", "Tortilla", "Queso"), listOf("Cocer y licuar los frijoles.", "Preparar el recaudo.", "Integrar y hervir.", "Servir con tortilla, queso y chile."), "Su nombre reconoce al pueblo purépecha y se popularizó como referente gastronómico de Michoacán.", "tarasca")
    )
    private fun dish(id:String,name:String,region:Region,description:String,ingredients:List<String>,steps:List<String>,history:String,image:String,video:String?=null)=Dish(id,name,region,description,ingredients,steps,history,image,video)
}
