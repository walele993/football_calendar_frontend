package com.walele.footballcalendarapp.data.mappers

import com.walele.footballcalendarapp.data.League
import com.walele.footballcalendarapp.data.local.CachedLeague
import com.walele.footballcalendarapp.network.models.LeagueDto

fun League.toCachedLeague(): CachedLeague {
    return CachedLeague(
        id = this.id,
        name = this.name,
        lastCachedTime = System.currentTimeMillis() // Impostiamo il timestamp di cache
    )
}

fun CachedLeague.toLeague(): League {
    return League(
        id = this.id,
        name = this.name
    )
}

fun LeagueDto.toLeague(): League {
    return League(
        id = this.id,
        name = this.name
    )
}
