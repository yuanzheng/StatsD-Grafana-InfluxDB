package com.lucas.monitoring.statsd.client

import scala.util.Random

import com.lucas.monitoring.statsd.client.configuration.ClientConfiguration

/**
  * StatsDClient trait companion object
  */
object StatsDClient {

    def apply(isEnabled: Boolean) = {
        (Option() filter (_ => isEnabled)).fold[StatsDClient[_]](NoOpClient())(_ => NonBlockingClient())
    }

}

/**
  * Trait defining the interface for communicating with a StatsD server.
  * @tparam R
  */
trait StatsDClient[R <: Any] {

    lazy val random = new Random


    /** Set a timed duration
      *
      * @param key Statistic key
      * @param duration How long the event took to
      * @param sampleRate Rate at which the counter should be sampled
      */
    def timing(key: String, duration: Long, sampleRate: Double = 1.0): Option[R]

    /** Time a unit of work.
      *
      * @param key Statistic key
      * @param unitOfWork Unit of work to be timed
      */
    def time[T](key: String)(unitOfWork: => T): T = {

        val start = currentTimeMillis()
        val result = unitOfWork
        val finish = currentTimeMillis()

        timing(key, finish - start)

        result
    }

    /**
      * Close the client connection
      */
    def close()

    /**
      * Probabilistically calls the unit of work. The unit of work will be called  {@code (rate * 100)%} of
      * the time.
      *
      * @param rate Percent of the time the unit of work should be called
      * @param unitOfWork Unit of work to call
      */
    def sample[R](rate: Double = 1.0)(unitOfWork: => R): Option[R] = {
        (Option() filter (_ => rate >= 1.0 || random.nextFloat < rate)).fold[Option[R]](None)(_ => Some(unitOfWork))
    }


    def currentTimeMillis() = System.currentTimeMillis()

}
