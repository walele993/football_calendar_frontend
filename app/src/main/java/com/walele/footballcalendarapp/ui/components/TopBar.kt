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
import androidx.compose.ui.text.font.FontWeight
import com.walele.footballcalendarapp.ui.theme.OnestVariableFont
import java.time.YearMonth

@Composable
fun TopBar(
    currentMonthYear: YearMonth,
    isYearlyView: Boolean,
    onViewToggle: () -> Unit,
    onMonthClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isYearlyView) {
                val formattedMonth = currentMonthYear.month.name
                    .lowercase()
                    .replaceFirstChar { it.uppercase() }

                Text(
                    text = formattedMonth,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = OnestVariableFont,
                        fontWeight = FontWeight.W900,
                        color = Color(0xFF383838) // Nero
                    ),
                    modifier = Modifier.clickable { onMonthClick() }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            val yearColor = if (isYearlyView) Color(0xFF383838) else Color(0xFFB0B0B0) // Nero se solo l'anno

            Text(
                text = "${currentMonthYear.year}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = OnestVariableFont,
                    fontWeight = FontWeight.W900,
                    color = yearColor
                ),
                modifier = Modifier.clickable { onMonthClick() }
            )
        }

        IconButton(onClick = onFilterClick) {
            Icon(
                Icons.Outlined.TravelExplore,
                contentDescription = "Select League",
                tint = Color(0xFF00A86B),
                modifier = Modifier
                    .fillMaxSize()

            )
        }
    }
}
