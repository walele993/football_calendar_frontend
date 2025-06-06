package com.walele.footballcalendarapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walele.footballcalendarapp.data.League
import com.walele.footballcalendarapp.data.LeagueRepository
import com.walele.footballcalendarapp.data.local.LeagueDao
import com.walele.footballcalendarapp.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LeagueViewModel(
    private val leagueRepository: LeagueRepository // Dipendenza passata tramite costruttore
) : ViewModel() {

    private val _leagues = MutableStateFlow<List<League>>(emptyList())
    val leagues: StateFlow<List<League>> = _leagues

    private val _selectedLeagues = MutableStateFlow<Set<League>>(emptySet())
    val selectedLeagues: StateFlow<Set<League>> = _selectedLeagues

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchLeagues()
    }

    private fun fetchLeagues() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val leagueList = leagueRepository.getAllLeagues()

                // Ordinare le leghe: prima quelle con "UEFA" e poi le altre in ordine alfabetico
                val sortedLeagues = leagueList.sortedWith { league1, league2 ->
                    when {
                        league1.name.contains("UEFA", ignoreCase = true) && !league2.name.contains("UEFA", ignoreCase = true) -> -1
                        !league1.name.contains("UEFA", ignoreCase = true) && league2.name.contains("UEFA", ignoreCase = true) -> 1
                        else -> league1.name.compareTo(league2.name)
                    }
                }

                _leagues.value = sortedLeagues
                Log.d("LeagueViewModel", "Leagues loaded: ${sortedLeagues.size} leagues")
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Error loading leagues: ${e.message}", e)
                _error.value = "Failed to load leagues"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLeagueSelection(league: League, isSelected: Boolean) {
        _selectedLeagues.value = if (isSelected) {
            _selectedLeagues.value + league
        } else {
            _selectedLeagues.value - league
        }
    }
}
