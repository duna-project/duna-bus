package io.duna.cluster.config

import com.typesafe.config.Config

case class ThreadingOptions(eventBusThreads: Int,
                            serverAcceptorThreads: Int,
                            workerThreads: Int)

object ThreadingOptions {
  def apply(config: Config): ThreadingOptions =
    new ThreadingOptions(config.getInt("eventbus"),
      config.getInt("acceptor"),
      config.getInt("worker"))
}
