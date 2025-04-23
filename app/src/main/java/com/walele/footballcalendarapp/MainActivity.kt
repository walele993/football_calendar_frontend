package com.walele.footballcalendarapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowInsetsControllerCompat
import com.walele.footballcalendarapp.ui.screens.HomeScreen
import com.walele.footballcalendarapp.ui.theme.FootballCalendarAppTheme
import androidx.core.view.WindowCompat
import com.walele.footballcalendarapp.network.ApiService
import com.walele.footballcalendarapp.data.MatchRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var matchRepository: MatchRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inizializzazione dell'API
        val apiService = Retrofit.Builder()
            .baseUrl("https://yourapiurl.com/")  // Usa l'URL della tua API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Crea MatchRepository con l'ApiService
        matchRepository = MatchRepository(apiService)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // L'inizializzazione della vista verrà fatta solo al ritorno nel ciclo di vita dell'attività
        setContent {
            FootballCalendarAppTheme {
                // Passa matchRepository alla HomeScreen
                HomeScreen(matchRepository = matchRepository)
            }
        }
    }

    @SuppressLint("WrongConstant")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        // Gestiamo l'inserimento della finestra solo quando la finestra è a fuoco
        if (hasFocus) {
            // Assicurati che le barre di sistema siano visibili
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Mostra le barre di stato e navigazione
                val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                insetsController.show(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
            } else {
                // Fallback per versioni precedenti ad Android 11
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }
    }
}
