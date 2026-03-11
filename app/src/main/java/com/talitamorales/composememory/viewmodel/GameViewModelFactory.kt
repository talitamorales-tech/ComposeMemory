package com.talitamorales.composememory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory (
    private val themeId: Int,
    private val difficultyId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(themeId, difficultyId) as T
    }
}
