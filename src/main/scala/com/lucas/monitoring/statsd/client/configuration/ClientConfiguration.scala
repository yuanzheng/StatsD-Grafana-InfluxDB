package com.lucas.monitoring.statsd.client.configuration

import com.lucas.monitoring.statsd.configuration.StatsDConfiguration

/**
  * Singleton for accessing client configurations
  */
object ClientConfiguration extends ClientConfiguration

trait ClientConfiguration extends StatsDConfiguration {

    override val predicate = getKey("client")

    /**
      * Determines if StatsD recording is enabled
      */
    lazy val isEnabled = getBoolean("enabled", false)




}
