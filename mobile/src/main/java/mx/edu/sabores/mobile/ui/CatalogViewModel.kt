package mx.edu.sabores.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.sabores.core.model.Region
import mx.edu.sabores.domain.ObserveCatalogUseCase
import mx.edu.sabores.domain.ToggleFavoriteUseCase

data class Filters(val query:String="", val region:Region?=null, val favoritesOnly:Boolean=false)

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModel(private val observeCatalog:ObserveCatalogUseCase, private val toggleFavorite:ToggleFavoriteUseCase):ViewModel(){
    private val filters=MutableStateFlow(Filters())
    val currentFilters=filters
    val items=filters.flatMapLatest { observeCatalog(it.query,it.region,it.favoritesOnly) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun search(value:String){ filters.value=filters.value.copy(query=value) }
    fun selectRegion(value:Region?){ filters.value=filters.value.copy(region=value) }
    fun showFavorites(value:Boolean){ filters.value=filters.value.copy(favoritesOnly=value) }
    fun toggle(id:String){ viewModelScope.launch { toggleFavorite(id) } }
}
