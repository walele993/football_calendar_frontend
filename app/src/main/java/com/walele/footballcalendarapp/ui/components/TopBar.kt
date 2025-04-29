package com.walele.footballcalendarapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import java.time.YearMonth

@Composable
fun TopBar(
    currentMonthYear: YearMonth,
    isYearlyView: Boolean,  // Aggiungi isYearlyView come parametro
    onViewToggle: () -> Unit,  // Callback per cambiare la vista
    onMonthClick: () -> Unit,   // Callback per il clic sul mese
    onFilterClick: () -> Unit   // Callback per il clic sul filtro
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isYearlyView) { // Mostra mese + anno solo nella vista mensile
                val formattedMonth = currentMonthYear.month.name.lowercase().replaceFirstChar { it.uppercase() }
                Text(
                    text = formattedMonth,
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF121212)),
                    modifier = Modifier.clickable { onMonthClick() } // Rendi cliccabile il mese
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            // Mostra solo l'anno
            Text(
                text = "${currentMonthYear.year}",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFFB0B0B0)),
                modifier = Modifier.clickable { onMonthClick() } // Rendi cliccabile anche l'anno
            )
        }
        IconButton(onClick = onFilterClick) {
            Icon(
                Icons.Outlined.TravelExplore,
                contentDescription = "Select League",
                tint = Color(0xFF00A86B),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
