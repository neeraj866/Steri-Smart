package com.medical.domain.executer

import io.reactivex.Scheduler

interface PostThreadExecutor{
    val scheduler : Scheduler
}