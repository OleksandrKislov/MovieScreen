package com.afishapet.moviescreen.data.datasource.megakino

data object MegakinoConfig {
    const val BASE_URL = "https://megakino.com.ua"
    const val USER_AGENT =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/116.0"
    const val BOOKING_BASE_URL =
        "https://w.megakino.com.ua/widget?userAgent=[userAgent]&siteId=[siteId]&showId=[showId]&eventId=[eventId]"
}