package com.diegoparra.kinodb.data.local

import androidx.room.TypeConverter
import com.diegoparra.kinodb.utils.LocalDateUtils
import com.diegoparra.kinodb.utils.LocaleUtils
import java.time.LocalDate
import java.util.Locale

class Converters {

    @TypeConverter
    fun toLocale(languageTag: String?): Locale? {
        return LocaleUtils.forLanguageTagOrNull(languageTag)
    }

    @TypeConverter
    fun fromLocale(locale: Locale?): String? {
        return locale?.toLanguageTag()
    }

    @TypeConverter
    fun toLocalDate(date: String?): LocalDate? {
        return LocalDateUtils.parseOrNull(date)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

}