package com.lucas.monitoring.statsd.client.networking

import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.{Callable, Executors}

import com.lucas.monitoring.statsd.client.configuration.ClientConfiguration

import scala.util.control.{Exception, NonFatal}

/** Provides an UDP connection to a StatsD server.  The constructor takes an optional function which will be used to handle
  * non-fatal exceptions (as defined by scala.util.control.NonFatal).  The default handler is a no-op function.
  *
  * The underlying socket is a JIT connection, thereby allowing the class to be instantiated without creating the socket.
  */
case class UdpConnection(nonFatalHandler: (Throwable => Unit) = (_: Throwable) => {}) extends Closeable with StatsDThreadFactoryProvider {

    lazy val configuration: ClientConfiguration = ClientConfiguration
    lazy val encoding = Charset.forName("UTF-8")

    lazy val handler = (exception: Throwable) => exception match {
        case NonFatal(exception) => {
            nonFatalHandler(exception)
            -1
        }
        case _ => throw exception
    }

    protected lazy val socket = {
        val socket = DatagramChannel.open()
        socket.connect(new InetSocketAddress(configuration.hostname, configuration.port))
    }

    lazy val executor = Executors.newSingleThreadExecutor(threadFactory)

    /** Close the UDP socket connection and shutdown the executor.  Once closed, the instance can no longer be used
      * to send messages.
      */
    override def close() = {
        Exception.ultimately {
            executor.shutdown()

            if (!executor.awaitTermination(3000, MILLISECONDS)) {
                executor.shutdownNow()
            }
        } apply {
            Exception.handling(classOf[Exception]) by handler apply {
                if (socket.isOpen()) socket.close()
            }
        }
    }

    /** Send a message over the existing socket.  The message will be scheduled with the executor and control will
      * be returned to the caller immediately.
      *
      * @param message Message to send
      */
    def send(message: String) = {
        executor.submit(new Callable[Int]() {
            override def call(): Int = {
                Exception.handling(classOf[Exception]) by handler apply {
                    socket.write(ByteBuffer.wrap(message.getBytes(encoding)))
                }
            }
        })
    }
}
