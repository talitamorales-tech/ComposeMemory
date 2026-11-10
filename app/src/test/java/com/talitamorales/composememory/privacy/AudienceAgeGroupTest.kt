package com.talitamorales.composememory.privacy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AudienceAgeGroupTest {
    @Test
    fun missingOrInvalidYearsNeverEnableFullScreenAds() {
        listOf(null, 2027, 0, 1899).forEach { birthYear ->
            val group = audienceForBirthYear(birthYear, 2026)
            assertEquals(AudienceAgeGroup.Unknown, group)
            assertFalse(group.canShowFullScreenAds)
        }
    }

    @Test
    fun smallChildrenAndTeenagersHaveTheSameProtection() {
        listOf(2026, 2024, 2023, 2022, 2010).forEach { birthYear ->
            val group = audienceForBirthYear(birthYear, 2026)
            assertEquals(AudienceAgeGroup.ChildOrTeen, group)
            assertFalse(group.canShowFullScreenAds)
        }
    }

    @Test
    fun eighteenthBirthdayIsNotAssumedFromBirthYearAlone() {
        val group = audienceForBirthYear(2008, 2026)
        assertEquals(AudienceAgeGroup.ChildOrTeen, group)
        assertFalse(group.canShowFullScreenAds)
    }

    @Test
    fun onlyAnUnambiguouslyAdultYearEnablesFullScreenAds() {
        val group = audienceForBirthYear(2007, 2026)
        assertEquals(AudienceAgeGroup.Adult, group)
        assertTrue(group.canShowFullScreenAds)
    }
}
