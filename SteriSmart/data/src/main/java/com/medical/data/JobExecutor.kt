package com.medical.data

import com.medical.domain.executer.ThreadExecutor
import java.util.concurrent.*

/**
 * Creates a thread pool to be used for each background task
 */
class JobExecutor : ThreadExecutor{

    companion object {

        private const val INITIAL_POOL_SIZE = 5

        private const val MAX_POOL_SIZE = 10

        // Sets the amount of time an idle thread waits before terminating
        private const val KEEP_ALIVE_TIME = 10

        // Sets the Time Unit to seconds
        private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
    }

    private val threadPoolExecutor : ThreadPoolExecutor
    private val threadFactory : ThreadFactory
    private val workQue : BlockingQueue<Runnable>

    init {
        threadFactory = JobThreadFactory()
        workQue = LinkedBlockingQueue()
        threadPoolExecutor = ThreadPoolExecutor(INITIAL_POOL_SIZE, MAX_POOL_SIZE,
            KEEP_ALIVE_TIME.toLong(), KEEP_ALIVE_TIME_UNIT, this.workQue, this.threadFactory)
    }

    override fun execute(command: Runnable) {
        threadPoolExecutor.execute(command)
    }

    /**
     * Span custom threads on request
     */
    inner class JobThreadFactory : ThreadFactory {
        override fun newThread(runable: Runnable): Thread {
           return Thread(runable)
        }

    }

}