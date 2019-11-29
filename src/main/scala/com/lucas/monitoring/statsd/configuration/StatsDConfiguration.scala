package com.lucas.monitoring.statsd.configuration

import com.typesafe.config.ConfigFactory

import scala.util.{Success, Try}

/**
  * Singleton for accessing all configurations
  */
object StatsDConfiguration extends StatsDConfiguration


trait StatsDConfiguration {

    /**
      * Default configuration prefix which will be used if one is not specified
      */
    val predicate: String = "statsd.client"

    /**
      * This can be used if a given configuration does not have a singleton for
      * accessing the configuration.
      */
    lazy val config = ConfigFactory.load()


    def getKey(key: String)(implicit predicate: String = this.predicate): String = predicate match {
        case empty if predicate.isEmpty => key
        case dotted if predicate.endsWith(".") => predicate + key
        case _ => predicate + "." + key
    }


    /** Get the configuration boolean value for the given key.
      *
      * @param key
      * @param default
      * @return
      */
    def getBoolean(key: String, default: Boolean = false) = (Try(config.getBoolean(getKey(key))) recoverWith { case _ => Success(default) }).get


}