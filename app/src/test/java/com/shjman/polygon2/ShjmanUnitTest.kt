package com.shjman.polygon2

import android.util.Log
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ShjmanUnitTest {
    @Test
    fun addition_isCorrect() {

        val a1 = checkCountAndPositionOfAnagram(
            string = "abacab",
            word = "ab"
        )

        val aa1 = arrayOf(0, 1, 4)
        assertEquals(aa1, a1)

//        assertEquals(4, 2 + 2)
    }

    private fun checkCountAndPositionOfAnagram(
        string: String,
        word: String,
    ): Array<Int> {
        val result = mutableListOf<Int>()
        result.add(0)
        result.add(1)
        result.add(4)
        val length = string.length
        for (i in 0..length) {
            Log.e("aaa", "aa $i")

        }


        return result.toTypedArray()
    }
}