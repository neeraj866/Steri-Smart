package com.medical.sterismart.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


/**
 *  Class that facilitates the connection with the api server
 */
class ApiConnection{

    companion object {
        var readTimeout : Long = 25
        var connectionTimeout : Long = 25
        var timeUnit = TimeUnit.SECONDS
    }

    /**
     * method that returns a httpClient instance for communication with api
     * @return OkHttpClient
     */
    private fun getClient() : OkHttpClient =  OkHttpClient.Builder()
        .readTimeout(readTimeout, timeUnit)
        .connectTimeout(connectionTimeout, timeUnit)
        .build()


    /**
     * method that returns in instance of ApiService which is used to call different endpoints from api server using retrofit
     * @param baseUrl the url to prepend api service endpoints
     * @return ApiService
     */
    fun retrofit(baseUrl : String = "https://logs.smartclave.ca/_/api/request/") : ApiService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder()))
        .client(getClient())
        .build().create(ApiService::class.java)


    /**
     * method that returns a Gson object which is used to convert Rest Objects to Gson entities
     */
    private fun gsonBuilder() : Gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setLenient().create()


}