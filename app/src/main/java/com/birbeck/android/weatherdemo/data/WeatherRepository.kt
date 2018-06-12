package com.birbeck.android.weatherdemo.data

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.birbeck.android.weatherdemo.BuildConfig
import com.birbeck.libdarksky.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.error

class WeatherRepository(application: Application) {

    enum class RefreshStatus { RUNNING, COMPLETE, ERROR }

    private val mWeatherDao = WeatherDatabase.getInstance(application).weatherDao()
    private val mRefreshStatus = MutableLiveData<RefreshStatus>()
    private val mLocationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        DarkSkyClient.debugLogging = BuildConfig.DEBUG
        DarkSkyClient.cacheDir = application.cacheDir
    }

    fun getCurrent() = mWeatherDao.getCurrent()

    fun refreshWeather(): MutableLiveData<RefreshStatus> {
        doAsync {
            val (lat, lng) = getLocation()
            val response = DarkSkyClient.getInstance(ApiKey(BuildConfig.DarkSkyApiKey))
                    .forecast(ForecastRequest(lat, lng,
                            exclude = listOf(Exclude.MINUTELY, Exclude.HOURLY, Exclude.ALERTS, Exclude.FLAGS)))
            when (response) {
                is Success -> {
                    val weatherEntity = WeatherEntity(temperature = response.forecast.currently?.temperature,
                            apparentTemperature = response.forecast.currently?.apparentTemperature,
                            summary = response.forecast.currently?.summary,
                            icon = response.forecast.currently?.icon,
                            highTemperature = response.forecast.daily?.data?.get(0)?.temperatureHigh,
                            lowTemperature = response.forecast.daily?.data?.get(0)?.temperatureLow,
                            time = response.forecast.currently?.time!!)
                    mWeatherDao.update(weatherEntity)
                    mRefreshStatus.postValue(RefreshStatus.COMPLETE)
                }
                is Error -> {
                    mRefreshStatus.postValue(RefreshStatus.ERROR)
                    AnkoLogger(this::class.java).error { response.error }
                }
            }
        }
        mRefreshStatus.postValue(RefreshStatus.RUNNING)
        return mRefreshStatus
    }

    private fun getLocation(): Array<Double> {
        var location: Location? = null
        try {
            location = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        } catch (e: SecurityException) {
            AnkoLogger(this::class.java).error { e.message }
        }
        return if (location != null)
            arrayOf(location.latitude, location.longitude)
        else
            arrayOf(37.774929, -122.419416)
    }
}
