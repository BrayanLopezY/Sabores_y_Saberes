package mx.edu.sabores.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import mx.edu.sabores.tv.ui.SaboresTvApp
import mx.edu.sabores.tv.ui.SaboresTvTheme

class TvMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SaboresTvTheme { SaboresTvApp() } }
    }
}
