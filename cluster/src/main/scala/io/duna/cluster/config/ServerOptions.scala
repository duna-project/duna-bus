package io.duna.cluster.config

import com.typesafe.config.Config

case class ServerOptions(host: String,
                         port: Int)

object ServerOptions {
  def apply(config: Config): ServerOptions =
    ServerOptions(config.getString("host"), config.getInt("port"))
}