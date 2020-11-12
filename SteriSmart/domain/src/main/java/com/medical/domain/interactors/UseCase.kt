package com.medical.domain.interactors

import com.medical.domain.executer.PostThreadExecutor
import com.medical.domain.executer.ThreadExecutor
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.schedulers.Schedulers


/**
 * Abstract class that facilitates to subscription of observers to observables
 * via specified threads
 * @param postThreadExecutor the thread to be used to return results
 * @param threadExecutor the thread to be used to do work
 */
abstract class UseCase<in T>(private val threadExecutor: ThreadExecutor, private val postThreadExecutor: PostThreadExecutor) {

   abstract fun buildUseCase(request : T) : Observable<*>

   /**
    * Method to do subscription to observable
    * @param request the request object to send to data layer
    * @param subscriber the observer to subscribe to the observable
    */
   fun execute(request : T) : Observable<out Any>? {
      return buildUseCase(request).subscribeOn(Schedulers.from(threadExecutor)).observeOn(postThreadExecutor.scheduler)
   }

}