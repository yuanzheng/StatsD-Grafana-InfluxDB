package com.lucas.monitoring.statsd

import client.configuration.ClientConfiguration
import client.StatsDClient

trait StatsD {

    lazy val configuration: ClientConfiguration = ClientConfiguration
    lazy val client: StatsDClient[_] = StatsDClient(configuration.isEnabled)

}
