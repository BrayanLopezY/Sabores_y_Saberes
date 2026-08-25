package mx.edu.sabores.tv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import mx.edu.sabores.domain.DishRepository

class TvCatalogViewModel(repository: DishRepository) : ViewModel() {
    val dishes = repository.observeDishes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
