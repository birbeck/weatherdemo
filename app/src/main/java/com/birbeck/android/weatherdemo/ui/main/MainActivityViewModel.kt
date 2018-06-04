package com.birbeck.android.weatherdemo.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v4.widget.SwipeRefreshLayout
import com.birbeck.android.weatherdemo.data.WeatherEntity
import com.birbeck.android.weatherdemo.data.WeatherRepository
import com.birbeck.android.weatherdemo.data.WeatherRepository.RefreshStatus
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository = WeatherRepository(application)
    private val mCurrentWeather = MediatorLiveData<WeatherEntity>()
    private val mViewLoaded = MutableLiveData<Boolean>()

    private var mExecutorService: ScheduledExecutorService? = null

    init {
        mCurrentWeather.value = null
        mViewLoaded.value = false
        mViewLoaded.observeForever { if (it == true) onViewLoaded() }
    }

    override fun onCleared() {
        super.onCleared()
        cancelWeatherUpdateJob()
    }

    fun getCurrent(): LiveData<WeatherEntity> = mCurrentWeather

    fun onRefresh(view: SwipeRefreshLayout) = mRepository.refreshWeather(getApplication())
            .observeForever {
                when (it) {
                    RefreshStatus.COMPLETE, RefreshStatus.ERROR -> view.isRefreshing = false
                    else -> {
                    }
                }
            }

    fun setViewLoaded(value: Boolean) {
        mViewLoaded.postValue(value)
    }

    fun setLocationPermissionGranted() {
        mCurrentWeather.value = null
        mRepository.refreshWeather(getApplication())
    }

    private fun onViewLoaded() {
        mCurrentWeather.addSource(mRepository.getCurrent(), { mCurrentWeather.value = it })
        scheduleWeatherUpdateJob()
    }

    private fun scheduleWeatherUpdateJob() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadScheduledExecutor()
                    .apply {
                        scheduleAtFixedRate({ mRepository.refreshWeather(getApplication()) },
                                15, 15, TimeUnit.MINUTES)
                    }
        }
    }

    private fun cancelWeatherUpdateJob() = mExecutorService?.shutdownNow()
}
