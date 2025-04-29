package com.walele.footballcalendarapp.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.geometry.Size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import kotlin.math.sin

fun calculateOpacity(matchCount: Int, maxMatchCount: Int): Float {
    val opacity = matchCount.toFloat() / maxMatchCount
    val adjustedOpacity = ((opacity * opacity * opacity) * 0.9f + 0.1f).coerceIn(0.1f, 0.4f)
    return adjustedOpacity
}

val SquircleShape: Shape = GenericShape { size: Size, _: LayoutDirection ->
    val width = size.width
    val height = size.height
    val roundness = 0.45f // tra 0 (rettangolo) e 1 (cerchio)

    val path = Path()
    val rx = width * roundness
    val ry = height * roundness

    path.moveTo(rx, 0f)
    path.lineTo(width - rx, 0f)
    path.quadraticBezierTo(width, 0f, width, ry)
    path.lineTo(width, height - ry)
    path.quadraticBezierTo(width, height, width - rx, height)
    path.lineTo(rx, height)
    path.quadraticBezierTo(0f, height, 0f, height - ry)
    path.lineTo(0f, ry)
    path.quadraticBezierTo(0f, 0f, rx, 0f)
    path.close()

    addPath(path)
}

@Composable
fun CalendarView(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate, Boolean) -> Unit,
    matchCountPerDay: Map<LocalDate, Int> = emptyMap(),
    maxMatchCount: Int = 1
) {
    val today = LocalDate.now()
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()

    val previousMonth = yearMonth.minusMonths(1)
    val daysInPreviousMonth = previousMonth.lengthOfMonth()
    val leadingDays = (daysInPreviousMonth - firstDayOfWeek + 1..daysInPreviousMonth).map {
        LocalDate.of(previousMonth.year, previousMonth.month, it)
    }
    val currentMonthDays = (1..daysInMonth).map {
        LocalDate.of(yearMonth.year, yearMonth.month, it)
    }
    val totalDays = leadingDays.size + currentMonthDays.size
    val trailingDaysCount = (7 - (totalDays % 7)).takeIf { it < 7 } ?: 0
    val nextMonth = yearMonth.plusMonths(1)
    val trailingDays = (1..trailingDaysCount).map {
        LocalDate.of(nextMonth.year, nextMonth.month, it)
    }
    val allDays = leadingDays + currentMonthDays + trailingDays
    val weeks = allDays.chunked(7)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        val weekDays = listOf("S", "M", "T", "W", "T", "F", "S")
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }
        }

        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    DayCell(
                        modifier = Modifier.weight(1f),
                        date = date,
                        isSelected = date == selectedDate,
                        isCurrentMonth = date.month == yearMonth.month,
                        isToday = date == today,
                        matchCount = matchCountPerDay[date] ?: 0,
                        maxMatchCount = maxMatchCount,
                        onClick = { onDateSelected(date, date.month == yearMonth.month) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    modifier: Modifier = Modifier,
    date: LocalDate,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    matchCount: Int,
    maxMatchCount: Int,
    onClick: () -> Unit
) {
    val selectionColor = Color(0xFF00A86B) // Arancione intenso

    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 500f
        ),
        label = "ElevationAnimation"
    )

    val animatedBarWidth by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = 700f
        ),
        label = "BarWidthAnimation"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ScaleAnimation"
    )

    val animatedOffsetY by animateDpAsState(
        targetValue = if (isSelected) (-2.dp) else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "OffsetYAnimation"
    )

    val targetTilt = if (isSelected) {
        val wave = sin((animatedScale * 10f).toDouble()) * 1.0
        wave.toFloat()
    } else {
        0f
    }

    val tilt by animateFloatAsState(
        targetValue = targetTilt,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = 0f),
        label = "Tilt"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Background squircle per match
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (matchCount > 0) {
                        val opacity = calculateOpacity(matchCount, maxMatchCount)
                        Color(0xFF00A86B).copy(alpha = opacity)
                    } else {
                        Color.Transparent
                    },
                    shape = SquircleShape
                )
        )

        // Contenuto principale scalato
        Box(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = animatedScale,
                    scaleY = animatedScale,
                    rotationZ = tilt
                )
        ) {
            Column(
                modifier = Modifier
                    .offset(y = animatedOffsetY),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isToday -> Color(0xFFFF6B00)
                        isSelected -> Color.Black
                        isCurrentMonth -> Color.Black
                        else -> Color.Gray
                    }
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .offset(y = 3.dp)
                            .width(animatedBarWidth)
                            .height(3.dp)
                            .background(color = selectionColor, shape = CircleShape)
                    )
                }
            }
        }
    }
}