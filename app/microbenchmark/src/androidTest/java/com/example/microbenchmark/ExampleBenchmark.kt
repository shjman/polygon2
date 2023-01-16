package com.example.microbenchmark

import android.util.Log
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity2::class.java)

    @Test
    fun scrollItem() {
        activityRule.scenario.onActivity { activity ->
            // If RecyclerView has children, the items are attached, bound, and gone through layout.
            // Ready to benchmark.
//            assertTrue("RecyclerView expected to have children", activity.binding.list.childCount > 0)

            benchmarkRule.measureRepeated {
//                activity.binding.list.scrollByOneItem()
//                runWithTimingDisabled {
//                    activity.testExecutor.flush()
//                }
            }
        }
    }
    
    @Test
    fun log() {
        benchmarkRule.measureRepeated {
            Log.d("LogBenchmark", "the cost of writing this log method will be measured")
        }
    }
}