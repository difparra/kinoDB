package com.diegoparra.kinodb.utils

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
class ListUtilsTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @After
    fun tearDown() = testDispatcher.cleanupTestCoroutines()


    @Test
    fun mapAsync_givenTransformFunction_checkMappingWasCorrect() = testDispatcher.runBlockingTest {
        val list = listOf(1, 2, 3, 4, 5)

        val result = list.mapAsync { it * 10 }

        assertThat(result).isEqualTo(listOf(10, 20, 30, 40, 50))
    }

    @Test
    fun mapAsync_runInParallel_spendLessTime() = testDispatcher.runBlockingTest {
        val list = listOf(1,2,3,4,5)
        val individualDelay = 1000L

        val timeBefore = this.currentTime
        val result = list.mapAsync {
            delay(individualDelay)
            it*10
        }
        val timeAfter = this.currentTime

        val aproxSequentialTime = individualDelay * list.size
        val totalTime = timeAfter - timeBefore

        println("Total time: $totalTime, Aprox sequential time: $aproxSequentialTime")
        assertThat(totalTime).isLessThan(aproxSequentialTime)
        assertThat(result).isEqualTo(listOf(10, 20, 30, 40, 50))
        assertThat(totalTime).isIn(1000L..2000L)
    }


}