package com.talitamorales.composememory.gamelogic

import com.talitamorales.composememory.R

enum class GameDifficulty(
    val id: Int,
    val pairCount: Int,
    val titleRes: Int
) {
    Easy(
        id = 1,
        pairCount = 4,
        titleRes = R.string.difficulty_easy
    ),
    Medium(
        id = 2,
        pairCount = 8,
        titleRes = R.string.difficulty_medium
    ),
    Hard(
        id = 3,
        pairCount = 12,
        titleRes = R.string.difficulty_hard
    );

    companion object {
        fun fromId(id: Int): GameDifficulty = entries.firstOrNull { it.id == id } ?: Easy
    }
}

