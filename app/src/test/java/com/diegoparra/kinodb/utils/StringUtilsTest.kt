package com.diegoparra.kinodb.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringUtilsTest {

    @Test
    fun removeCaseAndAccents_removeCase() {
        val word = "AbcDe"
        val result = word.removeCaseAndAccents()
        assertThat(result).isEqualTo("abcde")
    }

    @Test
    fun removeCaseAndAccents_removeAccents() {
        val word = "aéiòu"
        val result = word.removeCaseAndAccents()
        assertThat(result).isEqualTo("aeiou")
    }

    @Test
    fun removeCaseAndAccents_removeBoth() {
        val word = "AbcdÉ fgHíj"
        val result = word.removeCaseAndAccents()
        assertThat(result).isEqualTo("abcde fghij")
    }

    @Test
    fun removeCaseAndAccents_originalDoesNotContainNeitherCaseNorAccents() {
        val word = "custom word"
        val result = word.removeCaseAndAccents()
        assertThat(result).isEqualTo(word)
    }

}