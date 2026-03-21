package com.talitamorales.composememory.gamelogic

import com.talitamorales.composememory.R

enum class GameTheme(
    val id: Int,
    val titleRes: Int,
    val toolbarTitleRes: Int,
    val previewRes: Int,
    val isFree: Boolean
) {
    Animals(
        id = 1,
        titleRes = R.string.theme_animals,
        toolbarTitleRes = R.string.toolbar_theme_animals,
        previewRes = R.drawable.cat_persa,
        isFree = true
    ),
    Toys(
        id = 2,
        titleRes = R.string.theme_toys,
        toolbarTitleRes = R.string.toolbar_theme_toys,
        previewRes = R.drawable.toy_train,
        isFree = true
    ),
    Cars(
        id = 4,
        titleRes = R.string.theme_cars,
        toolbarTitleRes = R.string.toolbar_theme_cars,
        previewRes = R.drawable.car_mustang_preview,
        isFree = true
    ),
    Dolls(
        id = 7,
        titleRes = R.string.theme_dolls,
        toolbarTitleRes = R.string.toolbar_theme_dolls,
        previewRes = R.drawable.doll_princess_preview,
        isFree = true
    ),
    Music(
        id = 8,
        titleRes = R.string.theme_music,
        toolbarTitleRes = R.string.toolbar_theme_music,
        previewRes = R.drawable.music_piano,
        isFree = true
    ),
    Dinosaurs(
        id = 3,
        titleRes = R.string.theme_dinosaurs,
        toolbarTitleRes = R.string.toolbar_theme_dinosaurs,
        previewRes = R.drawable.dino_velociraptor,
        isFree = true
    ),
    Dogs(
        id = 5,
        titleRes = R.string.theme_dogs,
        toolbarTitleRes = R.string.toolbar_theme_dogs,
        previewRes = R.drawable.dog_lulu_pomerania,
        isFree = true
    ),
    JungleAnimals(
        id = 6,
        titleRes = R.string.theme_jungle_animals,
        toolbarTitleRes = R.string.toolbar_theme_jungle_animals,
        previewRes = R.drawable.jungle_lion,
        isFree = true
    );

    companion object {
        fun fromId(id: Int): GameTheme = entries.firstOrNull { it.id == id } ?: Animals
    }
}
