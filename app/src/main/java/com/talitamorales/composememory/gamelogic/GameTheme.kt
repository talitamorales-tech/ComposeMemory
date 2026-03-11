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
        previewRes = R.drawable.cat,
        isFree = true
    ),
    Toys(
        id = 2,
        titleRes = R.string.theme_toys,
        toolbarTitleRes = R.string.toolbar_theme_toys,
        previewRes = R.drawable.onboarding_plane,
        isFree = true
    ),
    Dinosaurs(
        id = 3,
        titleRes = R.string.theme_dinosaurs,
        toolbarTitleRes = R.string.toolbar_theme_dinosaurs,
        previewRes = R.drawable.onboarding_dino_logo,
        isFree = true
    ),
    FarmTractors(
        id = 4,
        titleRes = R.string.theme_farm_tractors,
        toolbarTitleRes = R.string.toolbar_theme_farm_tractors,
        previewRes = R.drawable.onboarding_farm_logo,
        isFree = true
    ),
    Dogs(
        id = 5,
        titleRes = R.string.theme_dogs,
        toolbarTitleRes = R.string.toolbar_theme_dogs,
        previewRes = R.drawable.dog,
        isFree = true
    );

    companion object {
        fun fromId(id: Int): GameTheme = entries.firstOrNull { it.id == id } ?: Animals
    }
}
