package com.diegoparra.kinodb.utils

/**
 *      Remove accents and convert to lower case. Ideal for searches.
 */
fun String.removeCaseAndAccents() =
    this.lowercase()
        .replace('á', 'a').replace('à', 'a')
        .replace('é', 'e').replace('à', 'a')
        .replace('í', 'i').replace('à', 'a')
        .replace('ó', 'o').replace('à', 'a')
        .replace('ú', 'u').replace('à', 'a')
        .replace('ñ', 'n')