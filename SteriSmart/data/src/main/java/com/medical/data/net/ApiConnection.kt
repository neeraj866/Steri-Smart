package com.medical.data.net

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * A connection class that facilitates the connection of application with
 * rest api using retrofit
 */
class ApiConnection{

    fun retrofit(baseUrl : String = "https://xrayqualityassurance.anvil.app/_/api/request/") : ApiService =
        Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient())
            .addConverterFactory( GsonConverterFactory.create(gsonCoverter()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(ApiService::class.java)


    /**
     * http client to be used for each request and response
     */
    private fun okHttpClient()  : OkHttpClient =
        OkHttpClient.Builder().readTimeout(5000,TimeUnit.MILLISECONDS)
            .writeTimeout(5000, TimeUnit.MILLISECONDS)
            .build()


    /**
     * gson coverter to be used to convert each request and response object to gson data
     */
    private fun gsonCoverter() : Gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setLenient().create()
}