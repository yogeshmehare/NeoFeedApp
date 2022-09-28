package com.yogesh.neofeedapp.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogesh.neofeedapp.model.models.Asteroid
import com.yogesh.neofeedapp.model.models.MainRepository
import kotlinx.coroutines.launch


class NeoFeedViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {


//    1.	Fastest Asteroid in km/h (Respective Asteroid ID & its speed)
//    2.	Closest Asteroid (Respective Asteroid ID & its distance)
//    3.	Average Size of the Asteroids in kilometers

    var fastest_asteroid = MutableLiveData("")
    var closest_asteroid = MutableLiveData("")
    var average_size_of_asteroid = MutableLiveData("")
    var start_date = MutableLiveData("____")
    var end_date = MutableLiveData("____")


    private var _asteroids: MutableLiveData<List<Asteroid>> = MutableLiveData(emptyList())
    val asteroids: LiveData<List<Asteroid>>
        get() = mainRepository.mAsteroidData

    fun search(view: View) {
        viewModelScope.launch {
            if (start_date.value != null && end_date.value != null) {
                mainRepository.getAsteroids(start_date.value!!, end_date.value!!)
            }
            Log.d("NeoApp", asteroids.value.toString())
        }
    }

}