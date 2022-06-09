package com.hackaprende.earthquakemonitor.api

import com.hackaprende.earthquakemonitor.Constants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface EqApiService {
    @GET(Constants.GET_LAST_HOUR_URL)
    suspend fun getLastHourEarthquakes(): EqJsonResponse
}

private var retrofit = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

var service: EqApiService = retrofit.create<EqApiService>(EqApiService::class.java)

/*
interface EarthquakeApiService {
    @GET(Constants.GET_LAST_HOUR_URL)
    suspend fun getLastHourEarthquakes(): EarthquakeJsonResponse
}

private val retrofit = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

object EarthquakesApi {
    val retrofitService : EarthquakeApiService by lazy {
        retrofit.create(EarthquakeApiService::class.java)
    }
}
 */