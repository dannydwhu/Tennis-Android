package com.smartform.tennis

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.smartform.tennis.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.smartform.tennis", appContext.packageName)
    }

    @Test
    fun testMainActivityLaunches() {
        // Verify MainActivity launches successfully
        val activity = activityRule.launchActivity(null)
        assertNotNull(activity)
        assertFalse(activity.isFinishing)
        assertFalse(activity.isDestroyed)
    }
}
