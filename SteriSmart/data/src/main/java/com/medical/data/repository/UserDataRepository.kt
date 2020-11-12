package com.medical.data.repository

import com.google.gson.Gson
import com.medical.data.net.ApiConnection
import com.medical.domain.Authenticate
import com.medical.domain.repository.UserRepository
import io.reactivex.Observable

class UserDataRepository : UserRepository {

    override fun authenticateUser(authenticate: Authenticate): Observable<Authenticate> {
        return ApiConnection().retrofit().authenticateUser(authenticate).toObservable()
    }
}