package com.diegoparra.kinodb.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class LocalDateUtilsTest {

    @Test
    fun parseOrNull_correctStringFormat_returnCorrectDate() {
        val dateStr = "2021-11-21"
        val result = LocalDateUtils.parseOrNull(dateStr)
        assertThat(result).isEqualTo(LocalDate.of(2021, 11, 21))
    }

    @Test
    fun parseOrNull_invalidDateFormat_returnNull() {
        assertThat(LocalDateUtils.parseOrNull(null)).isNull()
        assertThat(LocalDateUtils.parseOrNull("")).isNull()
        assertThat(LocalDateUtils.parseOrNull("2021-1121")).isNull()
    }

}