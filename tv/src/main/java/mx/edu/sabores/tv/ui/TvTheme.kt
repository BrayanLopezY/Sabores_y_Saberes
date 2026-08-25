package mx.edu.sabores.tv.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

/*
 * Paleta de alto contraste pensada para televisión.
 * Fondo casi negro + superficies claramente separadas + texto muy claro.
 * El dorado se usa para foco/acciones y el verde claro para información secundaria.
 */
private val TvColors = darkColorScheme(
    primary = Color(0xFFFFD166),
    onPrimary = Color(0xFF241700),
    secondary = Color(0xFF9EE7C1),
    onSecondary = Color(0xFF082218),
    background = Color(0xFF090B0D),
    onBackground = Color(0xFFFFF8F0),
    surface = Color(0xFF171A1F),
    onSurface = Color(0xFFFFF8F0),
    surfaceVariant = Color(0xFF2A3038),
    onSurfaceVariant = Color(0xFFFFFFFF),
    border = Color(0xFF8D98A7)
)

@Composable
fun SaboresTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = TvColors, content = content)
}
