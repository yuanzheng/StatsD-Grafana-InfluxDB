package com.lucas.monitoring.statsd.client

import java.util.concurrent.Future

/**
  * A StatsDClient implementation which communicates with the StatsD server on a background thread.  This
  * implementation is useful when StatsD has been enabled in the application configuration.
  */
case class NonBlockingClient() extends StatsDClient[Future[Int]] {

}
