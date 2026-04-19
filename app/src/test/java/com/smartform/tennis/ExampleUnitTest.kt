package com.smartform.tennis

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun verifyAppName() {
        // Verify app name constant
        val appName = "Smartform Tennis"
        assertNotNull(appName)
        assertTrue(appName.isNotEmpty())
    }
}