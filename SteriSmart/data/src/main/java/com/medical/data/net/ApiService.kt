package com.medical.data.net

import android.database.Observable
import com.medical.domain.Authenticate
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("technician/1?")
    fun authenticateUser(@Body request : Any) : Single<Authenticate>
}