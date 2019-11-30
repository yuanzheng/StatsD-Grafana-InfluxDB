package com.lucas.monitoring.statsd.client

import java.util.concurrent.Future
import com.lucas.monitoring.statsd.client.networking.UdpConnection
import configuration.ClientConfiguration

/** A StatsDClient implementation which communicates with the StatsD server on a background thread.  This
  * implementation is useful when StatsD has been enabled in the application configuration.
  */
case class NonBlockingClient() extends StatsDClient[Future[Int]] {


    lazy val handler = (t: Throwable) => {}

    lazy val connection = UdpConnection(handler)

    def close() = connection.close()

    def timing(key: String, duration: Long, rate: Double = 1.0) = sample(rate) {
        connection.send(generate(key, duration.toString(), "ms", rate))
    }

    /**
      * Client configuration singleton
      */
    lazy val configuration: ClientConfiguration = ClientConfiguration

    lazy val prefix = configuration.prefix.trim() match {
        case undotted if (!(configuration.prefix.isEmpty || configuration.prefix.endsWith("."))) => s"${undotted}."
        case other => other
    }

    /**
      * Generate a message string for sending to the StatsD server.
      *
      * @param key Statistic key
      * @param statistic Statistic
      * @param rate Rate at which the statistic was sampled
      */
    def generate(key: String, statistic: String, theType: String, rate: Double = 1.0) = {
        val body = s"${prefix}${key}:${statistic}|${theType}"

        (Option() filter (_ => rate < 1.0)).fold(body)(_ => s"${body}|@${rate}")
    }
}
