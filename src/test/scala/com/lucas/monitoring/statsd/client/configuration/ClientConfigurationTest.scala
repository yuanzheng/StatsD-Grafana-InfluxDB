package com.lucas.monitoring.statsd.client.configuration

import com.typesafe.config.Config
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}

class ClientConfigurationTest extends WordSpec with Matchers with MockFactory {

    trait BaseFixture {
        lazy val config = mock[Config]

        lazy val configuration = new ClientConfiguration {
            override lazy val config = BaseFixture.this.config
        }
    }

    trait UtilityMethodsFixture {

        lazy val getBoolean = mockFunction[String, Boolean, Boolean]
        lazy val getInt = mockFunction[String, Int, Int]
        lazy val getString = mockFunction[String, String, String]

        lazy val configuration = new ClientConfiguration {

            override def getBoolean(key: String, default: Boolean) = UtilityMethodsFixture.this.getBoolean(key, default)

            override def getInt(key: String, default: Int) = UtilityMethodsFixture.this.getInt(key, default)

            override def getString(key: String, default: String) = UtilityMethodsFixture.this.getString(key, default)
        }
    }

    "predicate" should {

        "be lucas.statsd.client" in {
            ClientConfiguration.predicate shouldBe "statsd.client"
        }
    }

    "config" should {

        "be an instance of Config" in {
            ClientConfiguration.config shouldBe a[Config]
        }
    }

    "isEnabled" should {

        "return the configuration" in new UtilityMethodsFixture {
            getBoolean expects("enabled", *) returning true

            configuration.isEnabled
        }

        "default to false" in new UtilityMethodsFixture {
            getBoolean expects(*, false) returning false

            configuration.isEnabled
        }
    }

    "prefix" should {

        "return the configuration" in new UtilityMethodsFixture {
            getString expects("stat.prefix", *) returning "default"

            configuration.prefix
        }

        "default to false" in new UtilityMethodsFixture {
            getString expects(*, "default") returning "default"

            configuration.prefix
        }
    }

    "hostname" should {

        "return the configuration" in new UtilityMethodsFixture {
            getString expects("hostname", *) returning "localhost"

            configuration.hostname
        }

        "default to false" in new UtilityMethodsFixture {
            getString expects(*, "localhost") returning "localhost"

            configuration.hostname
        }
    }

    "port" should {

        "return the configuration" in new UtilityMethodsFixture {
            getInt expects("port", *) returning 8125

            configuration.port
        }

        "default to false" in new UtilityMethodsFixture {
            getInt expects(*, 8125) returning 8125

            configuration.port
        }
    }
}
