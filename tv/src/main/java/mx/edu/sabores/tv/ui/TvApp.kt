package mx.edu.sabores.tv.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import mx.edu.sabores.core.model.Dish
import mx.edu.sabores.core.model.Region
import mx.edu.sabores.data.TvSyncClient
import mx.edu.sabores.tv.TvApplication

@Composable
fun SaboresTvApp() {
    val context = LocalContext.current
    val app = context.applicationContext as TvApplication
    val navController = rememberNavController()
    val catalogViewModel: TvCatalogViewModel = viewModel(
        factory = TvVmFactory { TvCatalogViewModel(app.container.repository) }
    )
    val tvSyncApi = remember { TvSyncClient.create() }
    var lastSelectionVersion by remember { mutableLongStateOf(-1L) }
    var receivedFromMobileId by remember { mutableStateOf<String?>(null) }

    // La TV consulta cada segundo si Mobile envió un platillo nuevo.
    // El campo version permite volver a enviar incluso el mismo platillo.
    LaunchedEffect(tvSyncApi) {
        while (true) {
            val selection = runCatching { tvSyncApi.getSelection() }.getOrNull()
            val selectedId = selection?.dishId

            if (
                selection != null &&
                selection.version > lastSelectionVersion &&
                !selectedId.isNullOrBlank()
            ) {
                lastSelectionVersion = selection.version
                val selectedDish = app.container.getDish(selectedId)
                if (selectedDish != null) {
                    Toast.makeText(
                        context,
                        "Recibido desde Mobile: ${selectedDish.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                    receivedFromMobileId = selectedDish.id
                    navController.navigate("detail/${selectedDish.id}") {
                        launchSingleTop = true
                    }
                }
            }

            delay(1_000L)
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            TvHomeScreen(catalogViewModel) { id ->
                receivedFromMobileId = null
                navController.navigate("detail/$id")
            }
        }
        composable(
            route = "detail/{dishId}",
            arguments = listOf(navArgument("dishId") { type = NavType.StringType })
        ) { entry ->
            TvDetailScreen(
                dishId = entry.arguments?.getString("dishId").orEmpty(),
                getDish = app.container.getDish,
                receivedFromMobile = entry.arguments?.getString("dishId") == receivedFromMobileId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun TvHomeScreen(viewModel: TvCatalogViewModel, onOpenDish: (String) -> Unit) {
    val dishes by viewModel.dishes.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 58.dp, end = 58.dp, top = 34.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        item {
            Column {
                Text("Sabores y Saberes", color = MaterialTheme.colorScheme.onBackground, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Text("Cocina tradicional y gastronomía patrimonial", color = MaterialTheme.colorScheme.secondary, fontSize = 18.sp)
            }
        }
        if (dishes.isNotEmpty()) {
            item { FeaturedDish(dishes.first(), onOpenDish) }
            Region.entries.forEach { region ->
                val regionalDishes = dishes.filter { it.region == region }
                if (regionalDishes.isNotEmpty()) {
                    item {
                        DishRow(region.displayName, regionalDishes, onOpenDish)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedDish(dish: Dish, onOpenDish: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Platillo destacado", color = MaterialTheme.colorScheme.onBackground, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        FocusableSurface(onClick = { onOpenDish(dish.id) }, modifier = Modifier.fillMaxWidth().height(245.dp)) {
            Row(Modifier.fillMaxSize()) {
                FoodArtwork(dish, Modifier.weight(1.1f).fillMaxHeight())
                Column(
                    Modifier.weight(1f).fillMaxHeight().padding(28.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(dish.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text(dish.region.displayName, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(dish.shortDescription, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(14.dp))
                    Text("Presiona OK para conocerlo", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
private fun DishRow(title: String, dishes: List<Dish>, onOpenDish: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp), contentPadding = PaddingValues(6.dp)) {
            items(dishes, key = { it.id }) { dish ->
                FocusableSurface(onClick = { onOpenDish(dish.id) }, modifier = Modifier.width(265.dp).height(190.dp)) {
                    Column {
                        FoodArtwork(dish, Modifier.fillMaxWidth().height(125.dp))
                        Column(Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                            Text(dish.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 19.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(dish.region.displayName, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FocusableSurface(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.045f else 1f, label = "focusScale")
    val borderColor by animateColorAsState(if (focused) MaterialTheme.colorScheme.primary else Color.Transparent, label = "focusBorder")
    val surfaceColor by animateColorAsState(
        if (focused) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
        label = "focusSurface"
    )
    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(surfaceColor)
            .border(3.dp, borderColor, RoundedCornerShape(18.dp))
            .onFocusChanged { focused = it.isFocused }
            .clickable(onClick = onClick),
        content = content
    )
}

@Composable
private fun TvDetailScreen(
    dishId: String,
    getDish: mx.edu.sabores.domain.GetDishUseCase,
    receivedFromMobile: Boolean,
    onBack: () -> Unit
) {
    var dish by remember(dishId) { mutableStateOf<Dish?>(null) }
    val context = LocalContext.current
    LaunchedEffect(dishId) { dish = getDish(dishId) }
    val current = dish ?: return Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Cargando…", color = MaterialTheme.colorScheme.onBackground) }

    Row(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(54.dp),
        horizontalArrangement = Arrangement.spacedBy(42.dp)
    ) {
        Column(Modifier.weight(.9f), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            FoodArtwork(current, Modifier.fillMaxWidth().height(320.dp).clip(RoundedCornerShape(22.dp)))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onBack) { Text("Volver") }
                current.videoUrl?.let { url ->
                    Button(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }) { Text("Reproducir video") }
                }
            }
        }
        LazyColumn(Modifier.weight(1.1f), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item {
                Text(current.name, color = MaterialTheme.colorScheme.onBackground, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                Text(current.region.displayName, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
                if (receivedFromMobile) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "📱 Recibido desde Mobile",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(current.shortDescription, color = MaterialTheme.colorScheme.onBackground, fontSize = 19.sp)
            }
            item { TvSection("Ingredientes", current.ingredients) }
            item { TvSection("Preparación", current.preparation.mapIndexed { index, step -> "${index + 1}. $step" }) }
            item {
                Text("Historia e importancia cultural", color = MaterialTheme.colorScheme.onBackground, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(current.culturalHistory, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun TvSection(title: String, lines: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        lines.forEach { Text("• $it", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp) }
    }
}

@Composable
private fun FoodArtwork(dish: Dish, modifier: Modifier = Modifier) {
    Box(modifier.background(Brush.linearGradient(artColors(dish.imageKey))), contentAlignment = Alignment.Center) {
        Text(artEmoji(dish.imageKey), fontSize = 76.sp)
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

class TvVmFactory<T : androidx.lifecycle.ViewModel>(
    private val creator: () -> T
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : androidx.lifecycle.ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
}
