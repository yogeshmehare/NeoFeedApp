package com.yogesh.neofeedapp.model.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.yogesh.neofeedapp.model.Constants.API_KEY
import com.yogesh.neofeedapp.model.`interface`.RetrofitService
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import kotlin.collections.ArrayList

class MainRepository constructor(private val retrofitService: RetrofitService){

    var mAsteroidData = MutableLiveData<List<Asteroid>>(emptyList())

    companion object {
        var dateListAsteroids = mutableMapOf<String,Int>()
    }

    suspend fun getAsteroids(startDate: String, endDate: String){
        withContext(Dispatchers.IO) {
            try {
                val asteroidData = retrofitService.getAsteroids(startDate, endDate, API_KEY)
                val asteroids:List<Asteroid> = parseAsteroidsJsonResult(JSONObject(asteroidData))
                mAsteroidData.postValue(asteroids)
            } catch (e: Exception) {
                Log.e("repository", e.cause.toString())
            }
        }
    }

    fun parseAsteroidsJsonResult(jsonResult: JSONObject): List<Asteroid> {
        val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")

        val asteroidList = ArrayList<Asteroid>()



        for (formattedDate in nearEarthObjectsJson.keys()) {
            val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)
            dateListAsteroids[formattedDate] = dateAsteroidJsonArray.length()

                for (i in 0 until dateAsteroidJsonArray.length()) {
                val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
                val id = asteroidJson.getLong("id")
                val codename = asteroidJson.getString("name")
                val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
                val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                    .getJSONObject("kilometers").getDouble("estimated_diameter_max")

                val closeApproachData = asteroidJson
                    .getJSONArray("close_approach_data").getJSONObject(0)
                val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                    .getDouble("kilometers_per_second")
                val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                    .getDouble("astronomical")
                val isPotentiallyHazardous = asteroidJson
                    .getBoolean("is_potentially_hazardous_asteroid")

                val asteroid = Asteroid(id, codename, formattedDate, absoluteMagnitude,
                    estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous)
                asteroidList.add(asteroid)
            }
        }

        return asteroidList
    }

}

