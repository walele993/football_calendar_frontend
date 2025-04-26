package com.walele.footballcalendarapp.ui.components

import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walele.footballcalendarapp.ui.viewmodel.LeagueViewModel
import com.walele.footballcalendarapp.data.League
import com.walele.footballcalendarapp.network.models.LeagueDto

@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    onApplyFilters: (List<Int>) -> Unit, // Pass the list of selected league IDs
    leagueViewModel: LeagueViewModel = viewModel()
) {
    val leagues by leagueViewModel.leagues.collectAsState()
    val selectedLeagues = remember { mutableStateOf<List<Int>>(emptyList()) }
    val isLoading by leagueViewModel.isLoading.collectAsState()

    if (isLoading) {
        // You can also add a loading indicator here
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Select Leagues:", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        if (leagues.isNotEmpty()) {
            leagues.forEach { league ->
                val isSelected = selectedLeagues.value.contains(league.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            if (isSelected) {
                                selectedLeagues.value = selectedLeagues.value.filter { it != league.id }
                            } else {
                                selectedLeagues.value = selectedLeagues.value + league.id
                            }
                        }
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = {
                            if (it) {
                                selectedLeagues.value = selectedLeagues.value + league.id
                            } else {
                                selectedLeagues.value = selectedLeagues.value.filter { id -> id != league.id }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = league.name)
                }
            }
        } else {
            Text("No leagues available.")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                onApplyFilters(selectedLeagues.value)
                onDismiss()
            }) {
                Text("Apply")
            }
        }
    }
}
