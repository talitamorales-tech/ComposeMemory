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
            Pair(R.drawable.toy_balloon, null),
            Pair(R.drawable.toy_car, null),
            Pair(R.drawable.toy_puzzle, null),
            Pair(R.drawable.toy_yoyo, null),
            Pair(R.drawable.toy_train, null),
            Pair(R.drawable.toy_plane_cartoon, null),
            Pair(R.drawable.toy_bear, null),
            Pair(R.drawable.toy_rocking_horse, null)
        )
        val carsAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.car_mustang, null),
            Pair(R.drawable.car_audi, null),
            Pair(R.drawable.car_ferrari, null),
            Pair(R.drawable.car_porsche, null),
            Pair(R.drawable.car_lamborghini, null),
            Pair(R.drawable.car_bmw, null),
            Pair(R.drawable.car_mercedes, null),
            Pair(R.drawable.car_golf, null),

        )
        val dollsAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.doll_princess, null),
            Pair(R.drawable.doll_mermaid, null),
            Pair(R.drawable.doll_baby, null),
            Pair(R.drawable.doll_ballerina, null),
            Pair(R.drawable.doll_barbie, null),
            Pair(R.drawable.doll_japanese, null),
            Pair(R.drawable.doll_rag, null),
            Pair(R.drawable.doll_fashion,null)

        )
        val musicAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.music_piano, null),
            Pair(R.drawable.music_saxophone, null),
            Pair(R.drawable.music_violin, null),
            Pair(R.drawable.music_guitar, null),
            Pair(R.drawable.music_accordion, null),
            Pair(R.drawable.music_pandero, null),
            Pair(R.drawable.music_bateria, null),
            Pair(R.drawable.music_baixo, null)
        )
        val dinosaursAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.dino_trex, null),
            Pair(R.drawable.dino_aerodactil, null),
            Pair(R.drawable.dino_fun, null),
            Pair(R.drawable.dino_estegossauro, null),
            Pair(R.drawable.dino_green, null),
            Pair(R.drawable.dino_triceratops, null),
            Pair(R.drawable.dino_apatosaurus, null),
            Pair(R.drawable.dino_velociraptor, null)
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
        val jungleAnimalsAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.jungle_elephant, null),
            Pair(R.drawable.jungle_monkey, null),
            Pair(R.drawable.jungle_zebra, null),
            Pair(R.drawable.jungle_lion, null),
            Pair(R.drawable.jungle_gorilla, null),
            Pair(R.drawable.jungle_tiger, null),
            Pair(R.drawable.jungle_giraffe, null),
            Pair(R.drawable.jungle_hipopotamo, null)
        )

        fun assetsForTheme(theme: GameTheme): List<Pair<Int, Int?>> = when (theme) {
            GameTheme.Animals -> animalsAssets
            GameTheme.Toys -> toysAssets
            GameTheme.Cars -> carsAssets
            GameTheme.Dolls -> dollsAssets
            GameTheme.Music -> musicAssets
            GameTheme.Dinosaurs -> dinosaursAssets
            GameTheme.Dogs -> dogsAssets
            GameTheme.JungleAnimals -> jungleAnimalsAssets
        }

        fun allThemeImageResources(): List<Int> {
            return (
                dinosaursAssets +
                    animalsAssets +
                    toysAssets +
                    carsAssets +
                    dollsAssets +
                    musicAssets +
                    dogsAssets +
                    jungleAnimalsAssets
                )
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
        .distinctBy { it.first to it.second }
    if (baseAssets.isEmpty()) return emptyList()

    val targetPairCount = difficulty.pairCount
    val uniquePairs = if (targetPairCount <= baseAssets.size) {
        baseAssets.shuffled().take(targetPairCount)
    } else {
        val shuffledPool = baseAssets.shuffled()
        List(targetPairCount) { index -> shuffledPool[index % shuffledPool.size] }.shuffled()
    }
    val doubled = uniquePairs.flatMap { listOf(it, it) }
    return createCards(doubled)
}
