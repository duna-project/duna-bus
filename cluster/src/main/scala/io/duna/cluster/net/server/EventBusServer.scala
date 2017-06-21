package io.duna.cluster.net.server

trait EventBusServer {

  def start(completionListener: => Unit = {}): Unit

  def stop(completionListener: => Unit = {}): Unit

}
