package com.lucas.monitoring.statsd

import client.configuration.ClientConfiguration
import client.StatsDClient

/** Provide a reference to StatsD singleton
  *
  */
trait StatsDProvider {
    lazy val statsd: StatsDClient[_] = StatsD.client
}

/** StatsD companion object
  *
  */
object StatsD extends StatsD

trait StatsD {

    lazy val configuration: ClientConfiguration = ClientConfiguration
    lazy val client: StatsDClient[_] = StatsDClient(configuration.isEnabled)

}
