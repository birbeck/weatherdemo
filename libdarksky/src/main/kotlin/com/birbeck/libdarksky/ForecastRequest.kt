package com.birbeck.libdarksky

data class ForecastRequest(val latitude: Double,
                           val longitude: Double,
                           val exclude: List<Exclude>? = null,
                           val extend: List<Extend>? = null,
                           val language: Language? = null,
                           val units: Units? = null)

enum class Extend(val value: String) {
    HOURLY("hourly")
}

enum class Language(val value: String) {
    EN("en"), ES("es")
}

enum class Exclude(val value: String) {
    CURRENTLY("currently"),
    MINUTELY("minutely"),
    HOURLY("hourly"),
    DAILY("daily"),
    FLAGS("flags"),
    ALERTS("alerts")
}

enum class Units(val value: String) {
    AUTO("auto"), CA("ca"), UK2("uk2"), US("us"), SI("si")
}
