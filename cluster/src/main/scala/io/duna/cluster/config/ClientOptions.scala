package io.duna.cluster.config

import com.typesafe.config.Config

case class ClientOptions(connectionPoolSize: Int)

object ClientOptions {
  def apply(config: Config): ClientOptions =
    ClientOptions(config.getInt("connectionPoolSize"))
}