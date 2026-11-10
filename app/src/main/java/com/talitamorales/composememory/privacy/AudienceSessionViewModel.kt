package com.talitamorales.composememory.privacy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AudienceSessionViewModel : ViewModel() {
    // Keep only the group in memory across rotations, never the entered birth year.
    var ageGroup: AudienceAgeGroup? by mutableStateOf(null)
        private set

    fun selectBirthYear(birthYear: Int?, currentYear: Int) {
        ageGroup = audienceForBirthYear(birthYear, currentYear)
    }
}
