package com.birbeck.android.weatherdemo.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.birbeck.android.weatherdemo.data.WeatherEntity
import com.birbeck.android.weatherdemo.data.WeatherRepository
import com.birbeck.android.weatherdemo.data.WeatherRepository.RefreshStatus
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val isLoading = MutableLiveData<Boolean>()
    val weather = MediatorLiveData<WeatherEntity>()

    private val mRepository = WeatherRepository(application)
    private var mExecutorService: ScheduledExecutorService? = null
    private val mRefreshStatus = MediatorLiveData<RefreshStatus>()
    private val mViewLoaded = MediatorLiveData<Boolean>()

    init {
        mRefreshStatus.observeForever {
            when (it) {
                RefreshStatus.RUNNING -> isLoading.value = true
                RefreshStatus.COMPLETE, RefreshStatus.ERROR -> isLoading.value = false
            }
        }

        val firstLoad = Transformations.map(mViewLoaded, {
            it?.takeIf { it }.also { onViewLoaded() }
            mViewLoaded.value = it
        })
        mViewLoaded.apply {
            addSource(firstLoad, {
                removeSource(firstLoad)
            })
            observeForever { }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelWeatherUpdateJob()
    }

    fun setViewLoaded(viewLoaded: Boolean) {
        mViewLoaded.value = viewLoaded
    }

    fun onViewLoaded() {
        val myLiveData = mRepository.refreshWeather()
        mRefreshStatus.addSource(myLiveData, {
            when (it) {
                RefreshStatus.COMPLETE, RefreshStatus.ERROR -> {
                    weather.addSource(mRepository.getCurrent(), { weather.value = it })
                    scheduleWeatherUpdateJob()
                    mRefreshStatus.removeSource(myLiveData)
                }
            }
            mRefreshStatus.value = it
        })
    }

    fun onRefresh() {
        val myLiveData = mRepository.refreshWeather()
        mRefreshStatus.addSource(myLiveData, {
            when (it) {
                RefreshStatus.COMPLETE, RefreshStatus.ERROR ->
                    mRefreshStatus.removeSource(myLiveData)
            }
            mRefreshStatus.value = it
        })
    }

    private fun scheduleWeatherUpdateJob() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadScheduledExecutor()
                    .apply {
                        scheduleAtFixedRate({
                            try {
                                mRepository.refreshWeather()
                            } catch (e: Exception) {
                                AnkoLogger(this::class.java).error { e }
                            }
                        }, 15, 15, TimeUnit.MINUTES)
                    }
        }
    }

    private fun cancelWeatherUpdateJob() = mExecutorService?.shutdownNow()
}
