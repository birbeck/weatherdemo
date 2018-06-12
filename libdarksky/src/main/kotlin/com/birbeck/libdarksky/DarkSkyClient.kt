package com.birbeck.libdarksky

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File

private const val CACHE_SIZE = 1 * 1024 * 1024L // 1 MiB

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

class DarkSkyClient private constructor(private val api: DarkSkyApi, private val apiKey: ApiKey) {

    companion object {
        var debugLogging = false
        var cacheDir: File? = null

        private val INSTANCES = HashMap<ApiKey, DarkSkyClient>()

        fun getInstance(apiKey: ApiKey): DarkSkyClient {
            if (!INSTANCES.containsKey(apiKey)) {
                val interceptor = HttpLoggingInterceptor()
                        .apply { if (debugLogging) level = HttpLoggingInterceptor.Level.BODY }
                val client = OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                if (cacheDir != null) {
                    client.cache(Cache(cacheDir, CACHE_SIZE))
                }

                val api = Retrofit.Builder()
                        .baseUrl("https://api.darksky.net/")
                        .client(client.build())
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build()
                        .create(DarkSkyApi::class.java)
                INSTANCES[apiKey] = DarkSkyClient(api, apiKey)
            }
            return INSTANCES[apiKey]!!
        }
    }

    fun forecast(request: ForecastRequest): ForecastResponse {
        val call = api.forecast(apiKey.value, request.latitude, request.longitude,
                exclude = request.exclude?.joinToString(",", transform = { it.value }),
                extend = request.extend?.joinToString(",", transform = { it.value }),
                language = request.language?.value, units = request.units?.value)
                .execute()
        return when {
            call.isSuccessful -> Success(call.body()!!)
            else -> Error(call.message())
        }
    }
}

data class ApiKey(val value: String)
