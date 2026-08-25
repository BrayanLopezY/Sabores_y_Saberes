package mx.edu.sabores.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import mx.edu.sabores.mobile.ui.SaboresApp
import mx.edu.sabores.mobile.ui.theme.SaboresTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { SaboresTheme { SaboresApp() } } }
}
