package com.walele.footballcalendarapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.walele.footballcalendarapp.data.Match

@Composable
fun MatchItem(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Visualizziamo la data della partita, la lega e il punteggio
            Text(
                text = "${match.date}  •  ${match.league}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "${match.homeTeam} vs ${match.awayTeam}",
                style = MaterialTheme.typography.bodyLarge
            )
            match.scoreHome?.let { scoreHome ->
                match.scoreAway?.let { scoreAway ->
                    Text(
                        text = "Score: $scoreHome - $scoreAway",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB0B0B0)
                    )
                }
            }
        }
    }
}
