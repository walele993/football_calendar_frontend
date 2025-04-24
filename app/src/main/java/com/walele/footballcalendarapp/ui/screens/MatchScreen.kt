package com.walele.footballcalendarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walele.footballcalendarapp.ui.components.MatchList
import com.walele.footballcalendarapp.ui.viewmodel.MatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen() {
    val viewModel: MatchViewModel = viewModel()
    val matches by viewModel.matches.collectAsState() // Osserva la lista delle partite
    val selectedDate by viewModel.selectedDate.collectAsState() // Osserva la data selezionata

    // Padding per la parte inferiore della lista
    val bottomPadding = 16.dp

    // La schermata principale
    Column(modifier = Modifier.fillMaxSize()) {
        // Titolo e data selezionata
        TopAppBar(
            title = { Text("Football Calendar") },
            modifier = Modifier.padding(16.dp)
        )

        // Lista dei match
        MatchList(
            matches = matches,
            selectedDate = selectedDate,
            bottomPadding = bottomPadding
        )
    }
}
