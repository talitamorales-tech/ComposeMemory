package com.talitamorales.composememory

import android.content.pm.PackageManager
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.talitamorales.composememory.privacy.AudienceAgeGroup
import com.talitamorales.composememory.privacy.AudienceSessionViewModel
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChildPrivacyInstrumentedTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun safeDefaultsAndOfflinePolicyAreAvailableBeforeAgeSelection() {
        val privacyLabel = compose.activity.getString(R.string.privacy_policy_title)
        val backLabel = compose.activity.getString(R.string.privacy_back)
        val continueLabel = compose.activity.getString(R.string.audience_continue)
        compose.onNodeWithText(continueLabel).assertIsNotEnabled()

        compose.onNodeWithText(privacyLabel).performClick()
        compose.onNodeWithText("talita.memoryland@gmail.com", substring = true).assertExists()
        compose.onNodeWithText(backLabel).performClick()
        compose.onNodeWithText(continueLabel).assertIsNotEnabled()

        compose.runOnIdle {
            val configuration = MobileAds.getRequestConfiguration()
            assertEquals(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE,
                configuration.tagForChildDirectedTreatment)
            assertEquals(RequestConfiguration.MAX_AD_CONTENT_RATING_G,
                configuration.maxAdContentRating)
            assertFalse(FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled)
            val permissions = compose.activity.packageManager.getPackageInfo(
                compose.activity.packageName, PackageManager.GET_PERMISSIONS
            ).requestedPermissions.orEmpty()
            assertFalse(permissions.contains("com.google.android.gms.permission.AD_ID"))
            assertFalse(permissions.any { it.startsWith("android.permission.ACCESS_ADSERVICES_") })
        }
    }

    @Test
    fun threeYearOldCanEnterTheGameWithFullScreenAdsDisabled() {
        val year = Calendar.getInstance().get(Calendar.YEAR) - 3
        compose.onNode(hasSetTextAction()).performTextInput(year.toString())
        compose.onNodeWithText(compose.activity.getString(R.string.audience_continue)).performClick()
        assertCanReachMenu(AudienceAgeGroup.ChildOrTeen)
    }

    @Test
    fun decliningAgeStillAllowsPlayingWithTheProtectiveDefault() {
        compose.onNodeWithText(compose.activity.getString(R.string.audience_skip)).performClick()
        assertCanReachMenu(AudienceAgeGroup.Unknown)
    }

    private fun assertCanReachMenu(expectedGroup: AudienceAgeGroup) {
        val title = compose.activity.getString(R.string.initial_choose_how_to_play)
        compose.waitUntil(timeoutMillis = 15_000) {
            compose.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(title).assertIsDisplayed()
        compose.runOnIdle {
            val group = ViewModelProvider(compose.activity)[AudienceSessionViewModel::class.java].ageGroup
            assertEquals(expectedGroup, group)
            assertFalse(group!!.canShowFullScreenAds)
        }
    }
}
