package com.walele.footballcalendarapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walele.footballcalendarapp.data.League
import com.walele.footballcalendarapp.data.LeagueRepository
import com.walele.footballcalendarapp.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LeagueViewModel : ViewModel() {

    private val leagueRepository = LeagueRepository(RetrofitInstance.apiService)

    private val _leagues = MutableStateFlow<List<League>>(emptyList())
    val leagues: StateFlow<List<League>> = _leagues

    private val _selectedLeagues = MutableStateFlow<Set<League>>(emptySet()) // Stato per le leghe selezionate
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
                _leagues.value = leagueList
                Log.d("LeagueViewModel", "Leagues loaded: ${leagueList.size} leagues")
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Error loading leagues: ${e.message}", e)
                _error.value = "Failed to load leagues"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Metodo per selezionare o deselezionare una lega
    fun toggleLeagueSelection(league: League, isSelected: Boolean) {
        _selectedLeagues.value = if (isSelected) {
            _selectedLeagues.value + league // Aggiungi la lega
        } else {
            _selectedLeagues.value - league // Rimuovi la lega
        }
    }
}
