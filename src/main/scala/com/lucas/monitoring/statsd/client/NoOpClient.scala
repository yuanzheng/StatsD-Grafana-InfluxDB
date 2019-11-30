package com.lucas.monitoring.statsd.client

/** A StatsDClient implementation which does not communicate with a StatsD server.  This implementation is useful
  * when StatsD has been disabled in the application configuration.
  */
case class NoOpClient() extends StatsDClient[Unit] {

    /**
      * Empty method which does nothing
      */
    def close() {}

    /**
      * Empty method which returning None
      */
    def count(key: String, delta: Long, sampleRate: Double = 1.0) = None

    /**
      * Empty method which returning None
      */
    def gauge(key: String, measurement: Double, sampleRate: Double = 1.0, isDelta: Boolean = false) = None

    /**
      * Empty method which returning None
      */
    def set(key: String, event: String, sampleRate: Double = 1.0) = None

    /**
      * Empty method which returning None
      */
    def timing(key: String, duration: Long, sampleRate: Double = 1.0) = None
}
