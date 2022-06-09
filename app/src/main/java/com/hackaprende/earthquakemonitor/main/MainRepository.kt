package com.hackaprende.earthquakemonitor.main

import com.hackaprende.earthquakemonitor.Earthquake
import com.hackaprende.earthquakemonitor.api.EarthquakeJsonResponse
import com.hackaprende.earthquakemonitor.api.EqJsonResponse
import com.hackaprende.earthquakemonitor.api.service
import com.hackaprende.earthquakemonitor.database.EqDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(private val database: EqDatabase) {

    suspend fun fetchEarthquakes(sortByMagnitude: Boolean): MutableList<Earthquake> {
        return withContext(Dispatchers.IO){
            val eqJsonResponse = service.getLastHourEarthquakes()
            val eqList = parseEqResult(eqJsonResponse)

            database.eqDao.insertAll(eqList)

            fetchEarthquakesFromDb(sortByMagnitude)
        }
    }

    suspend fun fetchEarthquakesFromDb(sortByMagnitude: Boolean): MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {
            if (sortByMagnitude) {
                database.eqDao.getEarthquakeByMagnitude()
            } else {
                database.eqDao.getEarthquake()
            }
        }
    }

    private fun parseEqResult(eqJsonResponse: EqJsonResponse): MutableList<Earthquake> {

        val eqList = mutableListOf<Earthquake>()
        val featureList = eqJsonResponse.features

        for(feature in featureList){
            val properties = feature.properties

            val id = feature.id
            val magnitude = properties.mag
            val place = properties.place
            val time = properties.time

            val geometry = feature.geometry
            val longitude = geometry.longitude
            val latitude = geometry.latitude

            eqList.add(Earthquake(id, place, magnitude, time, longitude, latitude))
        }
        return eqList
    }

    private fun getEarthquakeListFromResponse(earthquakeJsonResponse: EarthquakeJsonResponse): MutableList<Earthquake> {
        val eqList = mutableListOf<Earthquake>()

        val features = earthquakeJsonResponse.features
        for (feature in features) {
            val id = feature.id
            val place = feature.properties.place
            val magnitude = feature.properties.mag
            val time = feature.properties.time
            val latitude = feature.geometry.latitude
            val longitude = feature.geometry.longitude
            val earthquake = Earthquake(id, place, magnitude, time, latitude, longitude)
            eqList.add(earthquake)
        }

        return eqList
    }
}