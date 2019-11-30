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

    /**
      * Get the configuration integer value for the given key.
      *    *
      * @param key The configuration key.  The key should not contain the configuration's predicate.
      * @param default Default value to use if the configuration is not set or not an integer
      * @return The configured value or the default if no value is configured
      */
    def getInt(key: String, default: Int = 0) = (Try(config.getInt(getKey(key))) recoverWith { case _ => Success(default) }).get

    /**
      * Get the configuration string value for the given key.
      *    *
      * @param key The configuration key.  The key should not contain the configuration's predicate.
      * @param default Default value to use if the configuration is not set
      * @return The configured value or the default if no value is configured
      */
    def getString(key: String, default: String = "") = (Try(config.getString(getKey(key))) recoverWith { case _ => Success(default) }).get

}