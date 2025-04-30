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
import com.walele.footballcalendarapp.data.LeagueRepository
import com.walele.footballcalendarapp.data.local.AppDatabase
import androidx.room.Room
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var matchRepository: MatchRepository
    private lateinit var leagueRepository: LeagueRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inizializzazione dell'API
        val apiService = Retrofit.Builder()
            .baseUrl("https://football-calendar-backend.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Inizializzazione del database e del MatchDao
        val appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "football-calendar-db"
        ).build()

        val matchDao = appDatabase.matchDao()
        val leagueDao = appDatabase.leagueDao()

        // Crea MatchRepository con l'ApiService e MatchDao
        matchRepository = MatchRepository(apiService, matchDao)
        // Crea LeagueRepository con l'ApiService e AppDatabase
        leagueRepository = LeagueRepository(apiService, leagueDao)

        // Impostazione delle finestre per adattarsi alla UI
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Imposta il contenuto della schermata
        setContent {
            FootballCalendarAppTheme {
                // Passa matchRepository e leagueRepository alla HomeScreen
                HomeScreen(matchRepository = matchRepository, leagueRepository = leagueRepository)
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
