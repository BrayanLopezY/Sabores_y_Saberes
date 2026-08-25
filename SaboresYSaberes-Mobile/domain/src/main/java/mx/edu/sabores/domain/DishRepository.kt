package mx.edu.sabores.domain

import kotlinx.coroutines.flow.Flow
import mx.edu.sabores.core.model.Dish

interface DishRepository {
    fun observeDishes(): Flow<List<Dish>>
    fun observeFavoriteIds(): Flow<Set<String>>
    suspend fun findById(id: String): Dish?
    suspend fun toggleFavorite(id: String)
}
