package com.diegoparra.kinodb.utils

data class Data<T>(
    val content: T,
    val source: Source
) {
    enum class Source { LOCAL, SERVER }
    companion object {
        fun <T> fromLocal(content: T) = Data(content, Source.LOCAL)
        fun <T> fromServer(content: T) = Data(content, Source.SERVER)
    }
}