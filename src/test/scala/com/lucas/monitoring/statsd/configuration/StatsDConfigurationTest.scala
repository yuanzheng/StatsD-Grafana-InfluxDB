package com.lucas.monitoring.statsd.configuration

import org.scalatest.WordSpec
import org.scalatest.Matchers
import org.scalamock.scalatest.MockFactory
import com.typesafe.config.Config
import com.lucas.monitoring.statsd.client.configuration.ClientConfiguration
import com.lucas.monitoring.statsd.configuration.StatsDConfiguration


class StatsDConfigurationTest extends WordSpec with Matchers with MockFactory {

    trait DefaultValues {
        lazy val key = "unit.test"
    }

    trait BaseFixture extends DefaultValues {
        lazy val config = mock[Config]

        lazy val configuration = new StatsDConfiguration {
            override lazy val config = BaseFixture.this.config
        }
    }

    "predicate" should {

        "be statsd.client" in {
            StatsDConfiguration.predicate shouldBe "statsd.client"
        }
    }

    "config" should {

        "be an instance of Config" in {
            StatsDConfiguration.config shouldBe a[Config]
        }
    }

    "getKey" should {

        "default to the Trait's predicate" in new DefaultValues {
            ClientConfiguration.getKey(key) shouldBe s"${ClientConfiguration.predicate}.${key}"
        }

        "handle empty predicates" in new DefaultValues {
            ClientConfiguration.getKey(key)("") shouldBe key
        }

        "handle predicates with a trailing period" in new DefaultValues {
            ClientConfiguration.getKey(key)("pred.") shouldBe s"pred.${key}"
        }

        "handle predicates without a trailing period" in new DefaultValues {
            ClientConfiguration.getKey(key)("pred") shouldBe s"pred.${key}"
        }
    }

    "getBoolean" should {

        "prepend the key with the predicate" in new BaseFixture {

            (config.getBoolean _) expects (s"statsd.client.${key}") returning true

            configuration.getBoolean(key, false)
        }

        "return the configuration" in new BaseFixture {

            (config.getBoolean _) expects (*) returning true

            configuration.getBoolean(key, false) shouldBe true
        }

        "default the 'default' named parameter to false" in new BaseFixture {

            (config.getBoolean _) expects (*) throwing new Exception()

            configuration.getBoolean(key) shouldBe false
        }

        "return the default configuration" in new BaseFixture {

            (config.getBoolean _) expects (*) throwing new Exception()

            configuration.getBoolean(key, true) shouldBe true
        }
    }

    "getInt" should {

        "prepend the key with the predicate" in new BaseFixture {

            (config.getInt _) expects (s"statsd.client.${key}") returning 1

            configuration.getInt(key, 2)
        }

        "return the configuration" in new BaseFixture {

            (config.getInt _) expects (*) returning 1

            configuration.getInt(key, 2) shouldBe 1
        }

        "default the 'default' named parameter to 0" in new BaseFixture {

            (config.getInt _) expects (*) throwing new Exception()

            configuration.getInt(key) shouldBe 0
        }

        "return the default configuration" in new BaseFixture {

            (config.getInt _) expects (*) throwing new Exception()

            configuration.getInt(key, 15) shouldBe 15
        }
    }

    "getString" should {

        "prepend the key with the predicate" in new BaseFixture {

            (config.getString _) expects (s"statsd.client.${key}") returning "unit.test"

            configuration.getString(key, "default")
        }

        "return the configuration" in new BaseFixture {

            (config.getString _) expects (*) returning "unit.test"

            configuration.getString(key, "default") shouldBe "unit.test"
        }

        "default the 'default' named parameter to 0" in new BaseFixture {

            (config.getString _) expects (*) throwing new Exception()

            configuration.getString(key) shouldBe empty
        }

        "return the default configuration" in new BaseFixture {

            (config.getString _) expects (*) throwing new Exception()

            configuration.getString(key, "unit.test") shouldBe "unit.test"
        }
    }
}