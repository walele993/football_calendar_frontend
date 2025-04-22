package com.walele.footballcalendarapp.ui.viewmodel

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

    // Stato per la lista delle partite
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    // Stato per la data selezionata
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    // Recupera i match per una data selezionata
    fun getMatches() {
        viewModelScope.launch {
            try {
                val matchList = matchRepository.getMatches()
                _matches.value = matchList // Aggiorna la lista dei match
            } catch (e: Exception) {
                _matches.value = emptyList() // Gestisci eventuali errori (es. API non disponibile)
            }
        }
    }

    // Cambia la data selezionata
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        getMatches() // Recupera i match per la nuova data
    }
}
