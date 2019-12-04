package com.lucas.monitoring.statsd.client

import java.nio.charset.Charset
import java.util.concurrent.{Callable, ExecutorService, Future, TimeUnit}

import com.lucas.monitoring.statsd.client.configuration.ClientConfiguration
import com.lucas.monitoring.statsd.client.networking.UdpConnection
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}

class NonBlockingClientTest extends WordSpec with Matchers with MockFactory {

    trait BaseFixture {

        lazy val executor = mock[ExecutorService]
        lazy val future = mock[Future[Int]]
        lazy val hostname = mockFunction[String]
        lazy val port = mockFunction[Int]

        lazy val connection = new UdpConnection() {

            override lazy val configuration = new ClientConfiguration {
                override lazy val hostname = BaseFixture.this.hostname()
                override lazy val port = BaseFixture.this.port()
            }
            override lazy val executor = BaseFixture.this.executor

            def _socket = socket
        }
    }

    trait HandlerFixture {

        lazy val nonFatalHandler = mockFunction[Throwable, Unit]

        lazy val connection = UdpConnection(nonFatalHandler)
    }

    trait ConnectedFixture extends BaseFixture {

        hostname expects() returning "localhost"
        port expects() returning 8125

        connection._socket.isOpen() shouldBe true
        connection._socket.isConnected() shouldBe true
    }

    "configuration" should {

        "be the StatsDConfiguration singleton" in {
            UdpConnection().configuration shouldBe theSameInstanceAs(ClientConfiguration)
        }
    }

    "encoding" should {

        "be UTF-8" in {
            UdpConnection().encoding.compareTo(Charset.forName("UTF-8")) shouldBe 0
        }
    }

    "handler" should {

        "handle any non-fatal exceptions" in new HandlerFixture {

            val exception = new Exception("unit.test")

            nonFatalHandler expects (exception)

            connection.handler(exception) shouldBe -1
        }

        "throw any fatal exception" in new HandlerFixture {

            val exception = new InterruptedException("unit.test")

            nonFatalHandler expects (*) never()

            val thrown = the[Exception] thrownBy connection.handler(exception)
            thrown.getMessage shouldBe "unit.test"
        }
    }


    "close" should {
        // TODO: Cannot fully test until ScalaMock 4.0 - need ability to mock classes with final methods

        "close the socket" in new ConnectedFixture {

            (executor.shutdown _) expects()
            (executor.awaitTermination _) expects(*, *) returning true

            connection.close()

            connection._socket.isConnected() shouldBe false
            connection._socket.isOpen() shouldBe false
        }

        "not close the socket if it's already closed" in new BaseFixture {

            hostname expects() returning "localhost"
            port expects() returning 8125

            connection._socket.close()

            (executor.shutdown _) expects()
            (executor.awaitTermination _) expects(*, *) returning true

            connection.close()

            connection._socket.isConnected() shouldBe false
            connection._socket.isOpen() shouldBe false
        }

        "shutdown the executor" in new ConnectedFixture {

            (executor.shutdown _) expects()
            (executor.awaitTermination _) expects(3000, TimeUnit.MILLISECONDS) returning true
            (executor.shutdownNow _) expects() never()

            connection.close()
        }

        "force executor shutdown after 3 second termination timeout" in new ConnectedFixture {

            (executor.shutdown _) expects()
            (executor.awaitTermination _) expects(3000, TimeUnit.MILLISECONDS) returning false
            (executor.shutdownNow _) expects()

            connection.close()
        }
    }

    "send" should {
        // TODO: Cannot fully test until ScalaMock 4.0 - need ability to mock classes with final methods

        "submit a callable" in new BaseFixture {

            (executor.submit(_: Callable[Int])) expects (*)

            connection.send("unit.test")
        }

        "write the message to the socket" in new ConnectedFixture {

            (executor.submit(_: Callable[Int])) expects (where { c: Callable[Int] =>
                c.call()
                true
            }) returning future

            connection.send("unit.test") shouldBe future
        }
    }
}

