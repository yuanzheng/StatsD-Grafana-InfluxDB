package com.lucas.monitoring.statsd.client.networking

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ThreadFactory

/** Trait which can be mixed in to provide an instance of the StatsDThreadFactory
  */
trait StatsDThreadFactoryProvider {
    lazy val threadFactory = StatsDThreadFactory()
}


case class StatsDThreadFactory() extends ThreadFactory {
    lazy val nextThreadId = new AtomicInteger

    /** Create a new named thread.
      *
      * @param runnable Runnable to execute on the thread
      */
    def newThread(runnable: Runnable) = {
        val thread = new Thread(runnable, s"StatsD-${nextThreadId.getAndIncrement()}")
        thread.setDaemon(true)
        thread
    }
}