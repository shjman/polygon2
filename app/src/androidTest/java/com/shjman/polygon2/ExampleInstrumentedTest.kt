package com.shjman.polygon2

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ShjmanInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val a1 = checkCountAndPositionOfAnagram(
            string = "abacab",
            word = "ab"
        )
        val aa1 = arrayOf(0, 1, 4)
        assertEquals(aa1, a1)
        val a2 = checkCountAndPositionOfAnagram(
            string = "abacab",
            word = "bca"
        )
        val aa2 = arrayOf(1, 3)
        assertEquals(aa2, a2)
    }

    private fun checkCountAndPositionOfAnagram(
        string: String,
        word: String,
    ): Array<Int> {
        val result = mutableListOf<Int>()
        val length = string.length
        val worldLength = word.length
        for (i in 0..length - worldLength) {
            val take = string.substring(i, i + worldLength)
            val sorted = take.toList().sorted()
            val sortedWord = word.toList().sorted()
            val xz = sorted == sortedWord
            Log.e("aaa", "aa $i take $take sorted $sorted sortedWord $sortedWord xz $xz")
            if (xz) {
                result.add(i)
            }
        }
        return result.toTypedArray()
    }
}
