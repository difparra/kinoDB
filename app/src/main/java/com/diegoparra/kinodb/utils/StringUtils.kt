package com.diegoparra.kinodb.utils

/**
 *      Remove accents and convert to lower case. Ideal for searches.
 */
fun String.removeCaseAndAccents() =
    this.lowercase()
        .replace('á', 'a').replace('à', 'a')
        .replace('é', 'e').replace('è', 'e')
        .replace('í', 'i').replace('ì', 'i')
        .replace('ó', 'o').replace('ò', 'o')
        .replace('ú', 'u').replace('ù', 'u')
        .replace('ñ', 'n')