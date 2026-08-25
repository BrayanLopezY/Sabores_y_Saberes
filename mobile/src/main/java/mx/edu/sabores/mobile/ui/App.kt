@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)

package mx.edu.sabores.mobile.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import mx.edu.sabores.core.model.Dish
import mx.edu.sabores.core.model.Region
import mx.edu.sabores.data.TvSelectionRequest
import mx.edu.sabores.data.TvSyncClient
import mx.edu.sabores.domain.CatalogItem
import mx.edu.sabores.domain.GetDishUseCase
import mx.edu.sabores.domain.ToggleFavoriteUseCase
import mx.edu.sabores.mobile.SaboresApplication

@Composable
fun SaboresApp() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as SaboresApplication

    val vm: CatalogViewModel = viewModel(
        factory = SimpleVmFactory {
            CatalogViewModel(
                app.container.observeCatalog,
                app.container.toggleFavorite
            )
        }
    )

    NavHost(
        navController = navController,
        startDestination = "catalog"
    ) {
        composable("catalog") {
            CatalogScreen(
                vm = vm,
                onOpen = { id -> navController.navigate("detail/$id") }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            DetailScreen(
                id = entry.arguments?.getString("id").orEmpty(),
                getDish = app.container.getDish,
                toggle = app.container.toggleFavorite,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun CatalogScreen(
    vm: CatalogViewModel,
    onOpen: (String) -> Unit
) {
    val list by vm.items.collectAsStateWithLifecycle()
    val filters by vm.currentFilters.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = !filters.favoritesOnly,
                    onClick = { vm.showFavorites(false) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Catálogo") }
                )
                NavigationBarItem(
                    selected = filters.favoritesOnly,
                    onClick = { vm.showFavorites(true) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Favoritos") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Sabores y Saberes", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Cocina tradicional de México",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            item {
                OutlinedTextField(
                    value = filters.query,
                    onValueChange = vm::search,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar platillo o región") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
            }

            item { RegionFilters(filters.region, vm::selectRegion) }

            if (list.isEmpty()) {
                item {
                    Box(
                        Modifier
                            .fillParentMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (filters.favoritesOnly) {
                                "Aún no tienes platillos favoritos."
                            } else {
                                "No se encontraron platillos."
                            }
                        )
                    }
                }
            }

            items(list, key = { it.dish.id }) { item ->
                DishCard(
                    item = item,
                    onFavorite = { vm.toggle(item.dish.id) },
                    onOpen = { onOpen(item.dish.id) }
                )
            }
        }
    }
}

@Composable
private fun RegionFilters(
    selected: Region?,
    onSelect: (Region?) -> Unit
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text("Todas") }
        )
        Region.entries.forEach { region ->
            FilterChip(
                selected = selected == region,
                onClick = { onSelect(region) },
                label = { Text(region.displayName) }
            )
        }
    }
}

@Composable
private fun DishCard(
    item: CatalogItem,
    onFavorite: () -> Unit,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column {
            FoodArtwork(item.dish, Modifier.fillMaxWidth().height(150.dp))
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(item.dish.name, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text(
                        item.dish.region.displayName,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        item.dish.shortDescription,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (item.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodArtwork(
    dish: Dish,
    modifier: Modifier = Modifier
) {
    val colors = artColors(dish.imageKey)
    Box(
        modifier.background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Text(artEmoji(dish.imageKey), fontSize = 62.sp)
        Surface(
            color = Color.Black.copy(alpha = .35f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                dish.region.displayName,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            )
        }
    }
}

@Composable
private fun DetailScreen(
    id: String,
    getDish: GetDishUseCase,
    toggle: ToggleFavoriteUseCase,
    onBack: () -> Unit
) {
    var dish by remember(id) { mutableStateOf<Dish?>(null) }
    var sendingToTv by remember(id) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tvSyncApi = remember { TvSyncClient.create() }

    LaunchedEffect(id) {
        dish = getDish(id)
    }

    val current = dish ?: return Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(current.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { scope.launch { toggle(id) } }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Guardar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item {
                FoodArtwork(current, Modifier.fillMaxWidth().height(230.dp))
            }

            item {
                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(current.shortDescription, fontSize = 18.sp)
                    Section("Ingredientes", current.ingredients)
                    Section(
                        "Preparación",
                        current.preparation.mapIndexed { index, step -> "${index + 1}. $step" }
                    )
                    Text(
                        "Historia e importancia cultural",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(current.culturalHistory)

                    Button(
                        onClick = {
                            scope.launch {
                                sendingToTv = true
                                val result = runCatching {
                                    tvSyncApi.selectDish(TvSelectionRequest(current.id))
                                }
                                sendingToTv = false

                                val message = if (result.isSuccess && result.getOrNull()?.ok == true) {
                                    "${current.name} enviado a la TV"
                                } else {
                                    "No se pudo conectar con la TV. Verifica que sync_server.py esté ejecutándose."
                                }
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !sendingToTv
                    ) {
                        Icon(Icons.Default.Tv, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (sendingToTv) "Enviando…" else "Ver en TV")
                    }

                    current.videoUrl?.let { url ->
                        Button(
                            onClick = {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Ver video")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    lines: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        lines.forEach { Text("• $it") }
    }
}

private fun artColors(key: String) = when (key) {
    "mole", "mole_poblano" -> listOf(Color(0xFF5D3026), Color(0xFFD08B48))
    "cochinita" -> listOf(Color(0xFFE97842), Color(0xFFF2C14E))
    "chiles" -> listOf(Color(0xFF2F6D52), Color(0xFFE7B44C))
    "birria" -> listOf(Color(0xFFA53A27), Color(0xFFE49A55))
    else -> listOf(Color(0xFF397A68), Color(0xFFEBC67A))
}

private fun artEmoji(key: String) = when (key) {
    "tlayuda", "torta" -> "🥙"
    "cochinita", "birria" -> "🍲"
    "sopa", "tarasca" -> "🥣"
    "chiles" -> "🌶️"
    "corundas" -> "🫔"
    else -> "🍛"
}

class SimpleVmFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
}
