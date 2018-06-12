package com.birbeck.android.weatherdemo.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "weather", indices = [(Index("time"))])
data class WeatherEntity(@PrimaryKey(autoGenerate = true) var id: Long? = null,
                         val temperature: Double?,
                         val apparentTemperature: Double?,
                         val highTemperature: Double?,
                         val lowTemperature: Double?,
                         val summary: String?,
                         val icon: String?,
                         val time: Long)
