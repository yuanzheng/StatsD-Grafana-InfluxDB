package com.lucas.monitoring.statsd.client.networking

case class UdpConnection(nonFatalHandler: (Throwable => Unit) = (_: Throwable) => {}) {

}
