package com.walele.footballcalendarapp.network.models

data class MatchResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<MatchDto>
)

