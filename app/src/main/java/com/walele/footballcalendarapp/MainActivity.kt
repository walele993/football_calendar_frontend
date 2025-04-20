package com.walele.footballcalendarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.walele.footballcalendarapp.ui.screens.HomeScreen
import com.walele.footballcalendarapp.ui.theme.FootballCalendarAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FootballCalendarAppTheme {
                HomeScreen()
            }
        }
    }
}
