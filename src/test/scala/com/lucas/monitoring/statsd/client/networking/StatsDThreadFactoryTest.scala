package com.lucas.monitoring.statsd.client.networking

import org.scalatest.WordSpec
import org.scalatest.Matchers
import org.scalamock.scalatest.MockFactory


class StatsDThreadFactoryTest extends WordSpec with Matchers with MockFactory {

    trait Fixture {
        lazy val runnable = mock[Runnable]

        lazy val factory = StatsDThreadFactory()
    }

    "provider" should {

        "provide a StatsDThreadFactory instance" in {
            (new StatsDThreadFactoryProvider {}).threadFactory shouldBe a[StatsDThreadFactory]
        }
    }

    "newThread" should {

        "set the thread name" in new Fixture {
            factory.newThread(runnable).getName shouldBe s"StatsD-${factory.nextThreadId.get - 1}"
        }

        "increment the thread id counter" in new Fixture {
            factory.nextThreadId.get shouldBe 0
            factory.newThread(runnable)
            factory.nextThreadId.get shouldBe 1
        }

        "set the thread as a daemon" in new Fixture {
            factory.newThread(runnable).isDaemon() shouldBe true
        }
    }
}
