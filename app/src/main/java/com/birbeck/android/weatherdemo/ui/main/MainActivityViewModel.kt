package com.birbeck.android.weatherdemo.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.birbeck.android.weatherdemo.data.WeatherEntity
import com.birbeck.android.weatherdemo.data.WeatherRepository
import com.birbeck.android.weatherdemo.data.WeatherRepository.RefreshStatus
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val isLoading = MutableLiveData<Boolean>()
    val weather = MediatorLiveData<WeatherEntity>()

    private val mRefreshStatus = MediatorLiveData<RefreshStatus>()
    private val mRepository = WeatherRepository(application)
    private var mExecutorService: ScheduledExecutorService? = null

    init {
        mRefreshStatus.observeForever {
            when (it) {
                RefreshStatus.RUNNING -> isLoading.value = true
                RefreshStatus.COMPLETE, RefreshStatus.ERROR -> isLoading.value = false
            }
        }

        val myLiveData = mRepository.refreshWeather()
        mRefreshStatus.addSource(myLiveData, {
            when (it) {
                RefreshStatus.COMPLETE -> {
                    weather.addSource(mRepository.getCurrent(), { weather.value = it })
                    scheduleWeatherUpdateJob()
                    mRefreshStatus.removeSource(myLiveData)
                }
            }
            mRefreshStatus.value = it
        })
    }

    override fun onCleared() {
        super.onCleared()
        cancelWeatherUpdateJob()
    }

    fun onRefresh() {
        val myLiveData = mRepository.refreshWeather()
        mRefreshStatus.addSource(myLiveData, {
            when (it) {
                RefreshStatus.COMPLETE, RefreshStatus.ERROR -> {
                    mRefreshStatus.removeSource(myLiveData)
                }
            }
            mRefreshStatus.value = it
        })
    }

    private fun scheduleWeatherUpdateJob() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadScheduledExecutor()
                    .apply {
                        scheduleAtFixedRate({ mRepository.refreshWeather() },
                                15, 15, TimeUnit.MINUTES)
                    }
        }
    }

    private fun cancelWeatherUpdateJob() = mExecutorService?.shutdownNow()
}
