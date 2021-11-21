package com.diegoparra.kinodb.utils

import android.content.Context
import com.diegoparra.kinodb.R
import java.net.UnknownHostException

fun Exception.getErrorMessage(context: Context): String =
    when (this) {
        is UnknownHostException -> context.getString(R.string.network_connection_error)
        else -> context.getString(R.string.unknown_error, message)
    }

fun Exception.getLogMessage(): String =
    """
        exception = $this,
        message = ${this.message},
        javaClass = ${this.javaClass}
    """.trimIndent()