package com.walele.footballcalendarapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walele.footballcalendarapp.data.Match
import com.walele.footballcalendarapp.data.MatchRepository
import com.walele.footballcalendarapp.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MatchViewModel : ViewModel() {

    private val matchRepository = MatchRepository(RetrofitInstance.apiService)

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    fun getMatches() {
        viewModelScope.launch {
            try {
                Log.d("MatchViewModel", "Fetching matches for selected date: ${_selectedDate.value}")
                val matchList = matchRepository.getMatches()
                _matches.value = matchList // Aggiorna la lista dei match
                Log.d("MatchViewModel", "Matches loaded: ${matchList.size} matches")
            } catch (e: Exception) {
                Log.e("MatchViewModel", "Error loading matches: ${e.message}", e)
                _matches.value = emptyList() // Gestisci eventuali errori (es. API non disponibile)
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        Log.d("MatchViewModel", "Date changed to: $date")
        getMatches() // Recupera i match per la nuova data
    }
}
