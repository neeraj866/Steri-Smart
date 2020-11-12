package com.medical.domain.interactors

import com.medical.domain.Authenticate
import com.medical.domain.executer.PostThreadExecutor
import com.medical.domain.executer.ThreadExecutor
import com.medical.domain.repository.UserRepository
import io.reactivex.Observable

/**
 * Login use case  to facilitate observing a login observable via repository
 */
class LoginUseCase(threadExecutor : ThreadExecutor, postThreadExecutor: PostThreadExecutor, private val userRepository: UserRepository) : UseCase<Authenticate>(threadExecutor,postThreadExecutor){

    
    override fun buildUseCase(request: Authenticate): Observable<*> {
        return userRepository.authenticateUser(request)
    }
}