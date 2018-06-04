package com.birbeck.libdarksky

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private interface DarkSkyApi {
    @GET("forecast/{key}/{latitude},{longitude}")
    fun forecast(@Path("key") key: String,
                 @Path("latitude") latitude: Double,
                 @Path("longitude") longitude: Double,
                 @Query("exclude") exclude: String? = null,
                 @Query("extend") extend: String? = null,
                 @Query("lang") language: String? = null,
                 @Query("units") units: String? = null)
            : Call<Forecast>
}

class DarkSkyClient private constructor(private val api: DarkSkyApi) {

    companion object {
        val exclude: List<Exclude>? = null
        var extend: List<Extend>? = null
        var language: Language? = null
        var units: Units? = null
        var debugLogging = false

        private var INSTANCE: DarkSkyApi? = null
        fun getInstance(): DarkSkyClient {
            if (INSTANCE == null) {
                val interceptor = HttpLoggingInterceptor()
                        .apply { if (debugLogging) level = HttpLoggingInterceptor.Level.BODY }
                val client = OkHttpClient.Builder()
                        .addInterceptor(interceptor).build()
                val converterFactory = MoshiConverterFactory.create()

                INSTANCE = Retrofit.Builder()
                        .baseUrl("https://api.darksky.net/")
                        .client(client)
                        .addConverterFactory(converterFactory)
                        .build()
                        .create(DarkSkyApi::class.java)
            }
            return DarkSkyClient(INSTANCE!!)
        }
    }

    fun forecast(request: ForecastRequest): Forecast {
        val call = api.forecast(request.key.value, request.location.latitude, request.location.longitude,
                exclude = request.exclude?.joinToString(",", transform = { it.value }),
                extend = request.extend?.joinToString(",", transform = { it.value }),
                language = request.language?.value, units = request.units?.value)
                .execute()
        call.takeIf { it.isSuccessful }.run {
            return call.body()!!
        }
    }
}
