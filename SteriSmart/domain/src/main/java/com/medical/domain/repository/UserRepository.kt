package com.medical.domain.repository

import com.medical.domain.Authenticate
import io.reactivex.Observable

interface UserRepository{

    fun authenticateUser(authenticate: Authenticate) : Observable<Authenticate>
}