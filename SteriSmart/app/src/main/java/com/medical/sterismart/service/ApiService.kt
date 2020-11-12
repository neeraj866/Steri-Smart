package com.medical.sterismart.service

import com.medical.sterismart.model.LogDir
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit Interface wrapper for Api
 * This interface provides all the api request for the Application
 * And handles the payload with specific entity type
 */
interface ApiService{

    @Headers("Content-Type: application/json")
    @GET("list?dir=/")
    fun getDeviceLogs() : Call<ArrayList<LogDir>>

    @Headers("Content-Type: application/json")
    @GET("/list")
    fun getDateDeviceLog(@Query("dir") datePath: String) : Call<List<LogDir>>

    @Headers("Content-Type: application/json")
    @GET("list/{logMonth}")
    fun getDeviceLogMonth(@Query("dir") year: String,@Path("logMonth") month: String) : Call<List<LogDir>>

    @Headers("Content-Type: application/json")
    @GET
    fun getDeviceLog(@Url logId: String) : Call<String>
}