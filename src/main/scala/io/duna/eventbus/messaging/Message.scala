package io.duna.eventbus.messaging

protected[eventbus] case class Message[+T](source: Option[String] = None,
                                           target: String,
                                           responseEvent: Option[String] = None,
                                           headers: Map[String, String],
                                           attachment: Option[T])