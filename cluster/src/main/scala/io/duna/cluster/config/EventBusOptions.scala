package io.duna.cluster.config

import com.typesafe.config.{Config, ConfigFactory}

case class EventBusOptions(threadingOptions: ThreadingOptions,
                           serverOptions: ServerOptions,
                           clientOptions: ClientOptions,
                           sslOptions: Option[SslOptions] = None)

object EventBusOptions {
  def apply(): EventBusOptions = {
    val config = ConfigFactory.load()

    EventBusOptions(
      ThreadingOptions(config.getConfig("threads")),
      ServerOptions(config.getConfig("server")),
      ClientOptions(config.getConfig("client")),
      if (config.getBoolean("ssl.enabled"))
        Some(SslOptions(config.getConfig("ssl")))
      else None
    )
  }
}



