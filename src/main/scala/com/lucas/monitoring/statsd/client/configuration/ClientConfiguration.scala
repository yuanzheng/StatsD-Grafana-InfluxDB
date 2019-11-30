package com.lucas.monitoring.statsd.client.configuration

import com.lucas.monitoring.statsd.configuration.StatsDConfiguration

/**
  * Singleton for accessing client configurations
  */
object ClientConfiguration extends ClientConfiguration

trait ClientConfiguration extends StatsDConfiguration {

    /**
      * Determines if StatsD recording is enabled
      */
    lazy val isEnabled = getBoolean("enabled", false)

    /**
      * StatsD server hostname
      */
    lazy val hostname = getString("hostname", "localhost")

    /**
      * StatsD server port
      */
    lazy val port = getInt("port", 8125)

    /**
      * Statistic key prefix
      */
    lazy val prefix = getString("stat.prefix", "default")
}
