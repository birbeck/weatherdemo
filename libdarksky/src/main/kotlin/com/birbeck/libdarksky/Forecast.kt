package com.birbeck.libdarksky

import com.squareup.moshi.Json

data class Forecast(val latitude: Double,
                    val longitude: Double,
                    val timezone: String,
                    val currently: Currently? = null,
                    val minutely: Minutely? = null,
                    val hourly: Hourly? = null,
                    val daily: Daily? = null,
                    val alerts: List<Alert>? = null,
                    val flags: Flags? = null)

data class Currently(val time: Long,
                     val summary: String,
                     val icon: String,
                     val nearestStormDistance: Double? = null,
                     val nearestStormBearing: Double? = null,
                     val precipIntensity: Double? = null,
                     val precipProbability: Double? = null,
                     val temperature: Double? = null,
                     val apparentTemperature: Double? = null,
                     val dewPoint: Double? = null,
                     val humidity: Double? = null,
                     val pressure: Double? = null,
                     val windSpeed: Double? = null,
                     val windGust: Double? = null,
                     val windBearing: Int? = null,
                     val cloudCover: Double? = null,
                     val uvIndex: Int? = null,
                     val visibility: Double? = null,
                     val ozone: Double? = null)

data class Minutely(val summary: String,
                    val icon: String,
                    val data: List<MinutelyData>)

data class Hourly(val summary: String,
                  val icon: String,
                  val data: List<HourlyData>)

data class Daily(val summary: String,
                 val icon: String,
                 val data: List<DailyData>)

data class Flags(val sources: List<String>,
                 val units: String,
                 @Json(name = "darksky-unavailable") val darkSkyUnavailable: Boolean? = null)

data class Alert(val title: String,
                 val time: Long,
                 val expires: Long,
                 val description: String,
                 val uri: String,
                 val regions: List<String>,
                 val severity: Severity? = null) {
    enum class Severity {
        @Json(name = "advisory")
        ADVISORY,
        @Json(name = "warning")
        WARNING,
        @Json(name = "watch")
        WATCH
    }
}

data class MinutelyData(val time: Long,
                        val precipIntensity: Double? = null,
                        val precipProbability: Double? = null)

data class HourlyData(val time: Long,
                      val summary: String? = null,
                      val icon: String? = null,
                      val precipIntensity: Double? = null,
                      val precipProbability: Double? = null,
                      val temperature: Double? = null,
                      val apparentTemperature: Double,
                      val dewPoint: Double? = null,
                      val humidity: Double? = null,
                      val pressure: Double? = null,
                      val windSpeed: Double? = null,
                      val windGust: Double? = null,
                      val windBearing: Int? = null,
                      val cloudCover: Double? = null,
                      val uvIndex: Int? = null,
                      val visibility: Double? = null,
                      val ozone: Double? = null)

data class DailyData(val time: Long,
                     val summary: String? = null,
                     val icon: String? = null,
                     val sunriseTime: Long? = null,
                     val sunsetTime: Long? = null,
                     val moonPhase: Double? = null,
                     val precipIntensity: Double? = null,
                     val precipIntensityMax: Double? = null,
                     val precipIntensityMaxTime: Long? = null,
                     val precipProbability: Double? = null,
                     val precipType: String? = null,
                     val temperatureHigh: Double? = null,
                     val temperatureHighTime: Long? = null,
                     val temperatureLow: Double? = null,
                     val temperatureLowTime: Long? = null,
                     val apparentTemperatureHigh: Double? = null,
                     val apparentTemperatureHightTime: Long? = null,
                     val apparentTemperatureLow: Double? = null,
                     val apparentTemperatureLowTime: Long? = null,
                     val dewPoint: Double? = null,
                     val humidity: Double? = null,
                     val pressure: Double? = null,
                     val windSpeed: Double? = null,
                     val windGust: Double? = null,
                     val windGustTime: Long? = null,
                     val windBearing: Int? = null,
                     val cloudCover: Double? = null,
                     val uvIndex: Double? = null,
                     val uvIndexTime: Int? = null,
                     val visibility: Double? = null,
                     val ozone: Double? = null,
                     val temperatureMin: Double? = null,
                     val temperatureMinTime: Long? = null,
                     val temperatureMax: Double? = null,
                     val temperatureMaxTime: Long? = null)
