package com.walele.footballcalendarapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import com.walele.footballcalendarapp.ui.theme.OnestVariableFont
import kotlinx.coroutines.launch
import java.time.YearMonth
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.text.font.FontWeight

@Composable
fun BouncyClickable(
    onClick: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val animScale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    // LaunchedEffect per iniziare l'animazione prima di eseguire il clic
    val onClickWithAnimation = {
        coroutineScope.launch {
            // Iniziale "squash"
            animScale.snapTo(0.85f)
            animScale.animateTo(
                targetValue = 1.1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )

            // Ritornare a scala normale con overshoot
            animScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        // Esegui la funzione onClick solo dopo l'animazione
        onClick()
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = animScale.value
                scaleY = animScale.value
            }
            .clickable {
                onClickWithAnimation() // Trigger dell'animazione prima del click
            }
    ) {
        content(Modifier) // Il contenuto, che sarà animato
    }
}

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

                BouncyClickable(onClick = onMonthClick) { modifier ->
                    Text(
                        text = formattedMonth,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = OnestVariableFont,
                            fontWeight = FontWeight.W900,
                            color = Color(0xFF2a1e17)
                        ),
                        modifier = modifier
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            val yearColor = if (isYearlyView) Color(0xFF2a1e17) else Color(0xFFc2a189)

            BouncyClickable(onClick = onMonthClick) { modifier ->
                Text(
                    text = "${currentMonthYear.year}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = OnestVariableFont,
                        fontWeight = FontWeight.W900,
                        color = yearColor
                    ),
                    modifier = modifier
                )
            }
        }

        BouncyClickable(onClick = onFilterClick) { modifier ->
            Icon(
                imageVector = Icons.Outlined.TravelExplore,
                contentDescription = "Select League",
                tint = Color(0xFF2a1e17),
                modifier = modifier.size(32.dp)
            )
        }
    }
}
