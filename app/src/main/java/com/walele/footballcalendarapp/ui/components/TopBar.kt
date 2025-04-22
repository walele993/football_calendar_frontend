package com.walele.footballcalendarapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import java.time.YearMonth

@Composable
fun TopBar(
    currentMonthYear: YearMonth,
    onViewToggle: () -> Unit,  // Aggiungiamo il callback per cambiare la vista
) {
    val formattedMonth = currentMonthYear.month.name.lowercase().replaceFirstChar { it.uppercase() }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formattedMonth,
                style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF1F1F1F)),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${currentMonthYear.year}",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFFB0B0B0)),
            )
        }
        IconButton(onClick = onViewToggle) {
            Icon(Icons.Default.FilterList, contentDescription = "Toggle View", tint = Color.Black)
        }
    }
}
