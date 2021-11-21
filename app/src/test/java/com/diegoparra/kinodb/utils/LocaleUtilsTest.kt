package com.diegoparra.kinodb.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Locale

class LocaleUtilsTest {

    @Test
    fun forLanguageTagOrNull_correctStringFormat_returnCorrectDate() {
        val languageTag = "es"
        val result = LocaleUtils.forLanguageTagOrNull(languageTag)
        assertThat(result).isEqualTo(Locale.forLanguageTag("es"))
        assertThat(result?.toLanguageTag()).isEqualTo("es")
    }

    @Test
    fun forLanguageTagOrNull_invalidDateFormat_returnNull() {
        assertThat(LocaleUtils.forLanguageTagOrNull(null)).isNull()
        assertThat(LocaleUtils.forLanguageTagOrNull("")).isNull()
        assertThat(LocaleUtils.forLanguageTagOrNull(".zz")).isNull()
        assertThat(LocaleUtils.forLanguageTagOrNull("oiewqmopasdmf")).isNull()
    }

}