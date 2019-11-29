package com.lucas.monitoring.statsd.client

import scala.util.Random

import com.lucas.monitoring.statsd.client.configuration.ClientConfiguration

/**
  * StatsDClient trait companion object
  */
object StatsDClient {

    def apply(isEnabled: Boolean) = NonBlockingClient()

}

/**
  * Trait defining the interface for communicating with a StatsD server.
  * @tparam R
  */
trait StatsDClient[R <: Any] {

}
