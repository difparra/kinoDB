package com.diegoparra.kinodb.utils

import timber.log.Timber
import java.util.Locale

object LocaleUtils {
    fun forLanguageTagOrNull(languageTag: String?): Locale? {
        return if (languageTag.isNullOrEmpty()) {
            null
        } else {
            val locale = Locale.forLanguageTag(languageTag)
            return if(locale.toLanguageTag() == "und") {
                Timber.e("Couldn't parse languageTag: $languageTag")
                null
            } else {
                locale
            }
        }
    }
}