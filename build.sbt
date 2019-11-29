name := "StatsD-Grafana-InfluxDB"

version := "0.1"

scalaVersion := "2.12.7"

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

lazy val statsdClient = (project in file("."))
		.settings(
			name := "statsd-client",
			libraryDependencies += "com.typesafe" % "config" % "1.3.3",
		)
