package com.lucas.monitoring.statsd.client

import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.util.Random


class StatsDClientTest extends WordSpec with Matchers with MockFactory {

    trait RandomFixture {
        lazy val nextFloat = mockFunction[Float]

        lazy val random = new Random {
            override def nextFloat(): Float = RandomFixture.this.nextFloat()
        }
    }

    trait Fixture extends RandomFixture {
        lazy val key = "default.key"
        lazy val count = mockFunction[String, Long, Double, Option[Boolean]]
        lazy val timing = mockFunction[String, Long, Option[Boolean]]
        lazy val currentTimeMillis = mockFunction[Long]

        lazy val client = new StatsDClient[Boolean] {

            override lazy val random = Fixture.this.random

            def count(key: String, delta: Long, sampleRate: Double = 1.0) = Fixture.this.count(key, delta, sampleRate)

            def timing(key: String, duration: Long, sampleRate: Double = 1.0) = Fixture.this.timing(key, duration)

            override def currentTimeMillis() = Fixture.this.currentTimeMillis()

            def close() {}

            def gauge(key: String, measurement: Double, sampleRate: Double = 1.0, isDelta: Boolean) = None

            def set(key: String, event: String, sampleRate: Double = 1.0) = None
        }
    }

    trait SimpleStatsDClient {
        val client = new StatsDClient[Unit] {

            def close() {}

            def count(key: String, delta: Long, sampleRate: Double = 1.0) = None

            def gauge(key: String, measurement: Double, sampleRate: Double = 1.0, isDelta: Boolean = false) = None

            def set(key: String, event: String, sampleRate: Double = 1.0) = None

            def timing(key: String, duration: Long, sampleRate: Double = 1.0) = None
        }
    }

    "companion object" should {

        "provide an instance of the NoOpClient" in {
            StatsDClient(false) shouldBe a[NoOpClient]
        }

        "provide an instance of the NonBlockingClient" in {
            StatsDClient(true) shouldBe a[NonBlockingClient]
        }
    }

    "random" should {

        "be an instance of scala.util.Random" in new SimpleStatsDClient {
            client.random shouldBe a[Random]
        }
    }

    "time" should {

        "time the unit of work" in new Fixture {
            val unitOfWork = mockFunction[Integer]
            val start = 5
            val finish = 10

            inSequence {
                currentTimeMillis expects() returning start
                unitOfWork expects() returning 1
                currentTimeMillis expects() returning finish
                timing expects(key, finish - start) returning Option(true)
            }

            client.time(key)(unitOfWork()) shouldBe 1
        }

        "execute the unit of work" in new Fixture {
            val unitOfWork = mockFunction[Unit]

            currentTimeMillis expects() returning 0
            unitOfWork expects()
            timing expects(*, *) returning Option(true)
            currentTimeMillis expects() returning 0

            client.time(key)(unitOfWork())
        }
    }

    "sample" should {

        "run unit of work if rate equals or exceeds 1.0" in new Fixture with TableDrivenPropertyChecks {

            forAll(Table("rate", 1.0, 1.1)) { rate =>
                val unitOfWork = mockFunction[Unit]

                unitOfWork expects()

                client.sample(rate)(unitOfWork()) shouldBe Some()
            }
        }

        "run unit of work if sample is less than rate" in new Fixture {

            val unitOfWork = mockFunction[Unit]

            nextFloat expects() returning .49F
            unitOfWork expects()

            client.sample(.5)(unitOfWork()) shouldBe Some()
        }

        "not run unit of work if sample is greater than or equal to rate" in new Fixture with TableDrivenPropertyChecks {

            forAll(Table(
                ("rate", "sample"),
                (.5, .5F),
                (.5, .51F))) { (rate, sample) =>

                val unitOfWork = mockFunction[Unit]

                nextFloat expects() returning sample
                unitOfWork expects() never()

                client.sample(rate)(unitOfWork()) shouldBe None
            }
        }
    }
}
