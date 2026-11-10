package com.talitamorales.composememory.privacy

enum class AudienceAgeGroup {
    ChildOrTeen,
    Adult,
    Unknown;

    val canShowFullScreenAds: Boolean
        get() = this == Adult
}

fun audienceForBirthYear(birthYear: Int?, currentYear: Int): AudienceAgeGroup {
    if (birthYear == null || birthYear !in (currentYear - 125)..currentYear) {
        return AudienceAgeGroup.Unknown
    }

    // With only a birth year, treat everyone who might still be under 18 conservatively.
    return if (currentYear - birthYear > 18) {
        AudienceAgeGroup.Adult
    } else {
        AudienceAgeGroup.ChildOrTeen
    }
}
