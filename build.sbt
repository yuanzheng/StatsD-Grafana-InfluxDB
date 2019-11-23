name := "StatsD-Grafana-InfluxDB"

version := "0.1"

scalaVersion := "2.13.1"

lazy val statsdClient = (project in file("."))
		.settings(
			name := "statsd-client"
		)
