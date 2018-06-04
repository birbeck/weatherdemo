package com.birbeck.android.weatherdemo.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.location.LocationManager
import com.birbeck.android.weatherdemo.BuildConfig
import com.birbeck.libdarksky.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.error

class WeatherRepository(application: Application) {

    enum class RefreshStatus { RUNNING, COMPLETE, ERROR }

    private val mWeatherDao = WeatherDatabase.getInstance(application).weatherDao()
    private val mObservableWeather = MediatorLiveData<WeatherEntity>()
    private val mRefreshStatus = MutableLiveData<RefreshStatus>()

    init {
        DarkSkyClient.debugLogging = BuildConfig.DEBUG
        mObservableWeather.addSource<WeatherEntity>(mWeatherDao.getCurrent(), {
            if (it == null || it.time * 1000 < System.currentTimeMillis() - (15 * 60 * 1000)) {
                refreshWeather(application)
            } else {
                mObservableWeather.postValue(it)
            }
        })
    }

    fun getCurrent() = mObservableWeather

    fun refreshWeather(context: Context): LiveData<RefreshStatus> {
        val (lat, lng) = getLocation(context)
        doAsync {
            try {
                val forecast = DarkSkyClient.getInstance().forecast(ForecastRequest(
                        ApiKey(BuildConfig.DarkSkyApiKey), Location(lat, lng),
                        exclude = listOf(Exclude.MINUTELY, Exclude.HOURLY, Exclude.ALERTS, Exclude.FLAGS)))
                val weatherEntity = WeatherEntity(temperature = forecast.currently?.temperature!!,
                        apparentTemperature = forecast.currently?.apparentTemperature!!,
                        summary = forecast.currently?.summary!!,
                        icon = forecast.currently?.icon!!,
                        highTemperature = forecast.daily?.data?.get(0)?.temperatureHigh!!,
                        lowTemperature = forecast.daily?.data?.get(0)?.temperatureLow!!,
                        time = forecast.currently?.time!!)
                mWeatherDao.update(weatherEntity)
                mObservableWeather.postValue(weatherEntity)
                mRefreshStatus.postValue(RefreshStatus.COMPLETE)
            } catch (e: Exception) {
                mRefreshStatus.postValue(RefreshStatus.ERROR)
                AnkoLogger(this::class.java).error { e }
            }
        }
        mRefreshStatus.postValue(RefreshStatus.RUNNING)
        return mRefreshStatus
    }

    private fun getLocation(context: Context): Array<Double> {
        var location: android.location.Location? = null
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        } catch (e: SecurityException) {
            AnkoLogger(this::class.java).error { "$e" }
        }
        return if (location != null)
            arrayOf(location.latitude, location.longitude)
        else
            arrayOf(37.774929, -122.419416)
    }
}
