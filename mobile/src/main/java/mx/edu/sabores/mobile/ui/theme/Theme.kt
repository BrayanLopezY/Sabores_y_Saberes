package mx.edu.sabores.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Colors = lightColorScheme(primary=Color(0xFFA43B2A), onPrimary=Color.White, secondary=Color(0xFF2F6D52), background=Color(0xFFFFF8F1), surface=Color(0xFFFFFBF7), surfaceVariant=Color(0xFFF4E4D4), onBackground=Color(0xFF2B1D17), onSurface=Color(0xFF2B1D17))
@Composable fun SaboresTheme(content:@Composable ()->Unit){ MaterialTheme(colorScheme=Colors, content=content) }
