package com.birbeck.android.weatherdemo.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface WeatherDao {

    @Query("select * from weather order by time desc limit 1")
    fun getCurrent(): LiveData<WeatherEntity>

    @Query("delete from weather")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weatherEntity: WeatherEntity)

    @Transaction
    fun update(weatherEntity: WeatherEntity) {
        deleteAll()
        insert(weatherEntity)
    }
}
