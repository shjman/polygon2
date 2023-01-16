package com.shjman.polygon2

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shjman.polygon2.ui.MainActivity
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

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
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun myTest() {
        activityRule.scenario.onActivity { activity ->
//            benchmarkRule.measureRepeated {
                runBlocking {
                    val popularCategory1 = activity.spentViewModel.spentRepository.getPopularCategory()
                    val popularCategory11 = activity.spentViewModel.spentRepository.getPopularCategory()
                    val popularCategory2 = activity.spentViewModel.spentRepository.getPopularCategory2()
                    val popularCategory22 = activity.spentViewModel.spentRepository.getPopularCategory2()
                    Timber.e("aaaa1 ${popularCategory1.name}")
                    Timber.e("aaaa2 ${popularCategory11.name}")
                    Timber.e("aaaa3 ${popularCategory2.name}")
                    Timber.e("aaaa4 ${popularCategory22.name}")
                }
//            }
        }
    }
}
