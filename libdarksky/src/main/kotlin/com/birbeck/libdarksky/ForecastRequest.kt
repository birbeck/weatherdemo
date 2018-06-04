package com.birbeck.libdarksky

data class ForecastRequest(val key: ApiKey,
                           val location: Location,
                           val exclude: List<Exclude>? = DarkSkyClient.exclude,
                           val extend: List<Extend>? = DarkSkyClient.extend,
                           val language: Language? = DarkSkyClient.language,
                           val units: Units? = DarkSkyClient.units)

data class ApiKey(val value: String)

data class Location(val latitude: Double, val longitude: Double)

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
