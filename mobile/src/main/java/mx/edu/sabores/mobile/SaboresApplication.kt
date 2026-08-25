package mx.edu.sabores.mobile

import android.app.Application
import mx.edu.sabores.data.LocalDishRepository
import mx.edu.sabores.domain.GetDishUseCase
import mx.edu.sabores.domain.ObserveCatalogUseCase
import mx.edu.sabores.domain.ToggleFavoriteUseCase

class SaboresApplication : Application() {
    val container by lazy {
        val repository = LocalDishRepository(this)
        AppContainer(ObserveCatalogUseCase(repository), GetDishUseCase(repository), ToggleFavoriteUseCase(repository))
    }
}
data class AppContainer(val observeCatalog: ObserveCatalogUseCase, val getDish: GetDishUseCase, val toggleFavorite: ToggleFavoriteUseCase)
