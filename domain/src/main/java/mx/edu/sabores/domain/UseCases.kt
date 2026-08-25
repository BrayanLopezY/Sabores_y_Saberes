package mx.edu.sabores.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import mx.edu.sabores.core.model.Dish
import mx.edu.sabores.core.model.Region

data class CatalogItem(val dish: Dish, val favorite: Boolean)

class ObserveCatalogUseCase(private val repository: DishRepository) {
    operator fun invoke(query: String, region: Region?, favoritesOnly: Boolean): Flow<List<CatalogItem>> =
        combine(repository.observeDishes(), repository.observeFavoriteIds()) { dishes, favorites ->
            dishes.asSequence()
                .filter { query.isBlank() || it.name.contains(query, true) || it.region.displayName.contains(query, true) }
                .filter { region == null || it.region == region }
                .filter { !favoritesOnly || it.id in favorites }
                .map { CatalogItem(it, it.id in favorites) }
                .toList()
        }
}

class GetDishUseCase(private val repository: DishRepository) { suspend operator fun invoke(id: String) = repository.findById(id) }
class ToggleFavoriteUseCase(private val repository: DishRepository) { suspend operator fun invoke(id: String) = repository.toggleFavorite(id) }
