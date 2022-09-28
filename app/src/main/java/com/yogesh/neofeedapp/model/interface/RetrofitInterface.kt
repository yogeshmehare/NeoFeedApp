package com.yogesh.neofeedapp.model.`interface`

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yogesh.neofeedapp.model.Constants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") start_date:String,
        @Query("end_date") end_date:String,
        @Query("api_key") api_key: String
    ) : String

    companion object {
        var retrofitService: RetrofitService? = null
        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        fun getInstance() : RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
//                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}