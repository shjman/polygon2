package com.example.benchmark

import android.util.Log
import androidx.benchmark.macro.*
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @OptIn(ExperimentalMetricApi::class)
    @Test
    fun startup() = benchmarkRule.measureRepeated(
        compilationMode = CompilationMode.None(), // JIT - dalvik , AOT -ART

        packageName = "com.shjman.polygon2",

        metrics = listOf(
            StartupTimingMetric(),
            FrameTimingMetric(),
            TraceSectionMetric("Polygon2Theme"),
            TraceSectionMetric("getPopularCategory2"),
        ),
        iterations = 11,
        startupMode = StartupMode.WARM
    ) {
        pressHome()
        startActivityAndWait()
        clickOnButton()
    }

    private fun MacrobenchmarkScope.clickOnButton() {
        Log.e("dddd1", "ddd")
        device.findObject(By.res("go spent screen button")).click()

        check(device.wait(Until.hasObject(By.textContains("done1")), TimeUnit.SECONDS.toMillis(10)))
        device.waitForIdle()
        Log.e("dddd2", "ddd")
    }
}