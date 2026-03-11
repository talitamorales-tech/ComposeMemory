package com.talitamorales.composememory.gamelogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.talitamorales.composememory.R

data class Card(
    var id: Int,
    val imageRes: Int,
    val soundRes: Int?
) {
    companion object {
        val animalsAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.cat_bengal, null),
            Pair(R.drawable.cat_maine, null),
            Pair(R.drawable.cat_persa, null),
            Pair(R.drawable.cat_ragdoll, null),
            Pair(R.drawable.cat_siames, null),
            Pair(R.drawable.cat_sleep, null),
            Pair(R.drawable.cat_fofinho, null),
            Pair(R.drawable.cat_white, null)
        )
        val toysAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.ball, null),
            Pair(R.drawable.doll, null),
            Pair(R.drawable.toy_car, null),
            Pair(R.drawable.plane, null)
        )
        val dinosaursAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.dino_trex, null),
            Pair(R.drawable.dino_triceratops, null),
            Pair(R.drawable.dino_stego, null),
            Pair(R.drawable.dino_brachio, null)
        )
        val farmTractorsAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.toy_car, null),
            Pair(R.drawable.plane, null),
            Pair(R.drawable.ball, null),
            Pair(R.drawable.doll, null)
        )
        val dogsAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.dog_dachshund, null),
            Pair(R.drawable.dog_husky_siberiano, null),
            Pair(R.drawable.dog_lhasa_apso, null),
            Pair(R.drawable.dog_poodle, null),
            Pair(R.drawable.dog_shih_tzu, null),
            Pair(R.drawable.dog_german_shepherd, null),
            Pair(R.drawable.dog_lulu_pomerania, null),
            Pair(R.drawable.dog_yorkshire, null)
        )

        fun assetsForTheme(theme: GameTheme): List<Pair<Int, Int?>> = when (theme) {
            GameTheme.Animals -> animalsAssets
            GameTheme.Toys -> toysAssets
            GameTheme.Dinosaurs -> dinosaursAssets
            GameTheme.FarmTractors -> farmTractorsAssets
            GameTheme.Dogs -> dogsAssets
        }

        fun allThemeImageResources(): List<Int> {
            return (dinosaursAssets + animalsAssets + toysAssets + farmTractorsAssets + dogsAssets)
                .map { it.first }
                .distinct()
        }
    }
    var isFaceUp by mutableStateOf(false)
    var isMatched by mutableStateOf(false)
}

fun createCards(cards: List<Pair<Int, Int?>>): List<Card> {
    val ids = (1..5000).shuffled().take(cards.count())
    return cards.shuffled().mapIndexed {index, pair -> Card(id = ids[index], imageRes = pair.first, soundRes = pair.second)}
}

fun createCardsForTheme(theme: GameTheme, difficulty: GameDifficulty): List<Card> {
    val baseAssets = Card.assetsForTheme(theme)
    if (baseAssets.isEmpty()) return emptyList()

    val pairs = MutableList(difficulty.pairCount) { index ->
        baseAssets[index % baseAssets.size]
    }
    val doubled = pairs.flatMap { listOf(it, it) }
    return createCards(doubled)
}
