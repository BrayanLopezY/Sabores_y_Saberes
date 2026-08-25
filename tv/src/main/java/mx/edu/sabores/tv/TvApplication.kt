package mx.edu.sabores.tv

import android.app.Application
import mx.edu.sabores.data.LocalDishRepository
import mx.edu.sabores.domain.GetDishUseCase

class TvApplication : Application() {
    val container by lazy {
        val repository = LocalDishRepository(this)
        TvContainer(repository, GetDishUseCase(repository))
    }
}

data class TvContainer(
    val repository: mx.edu.sabores.domain.DishRepository,
    val getDish: GetDishUseCase
)
