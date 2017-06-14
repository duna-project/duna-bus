package io.duna.eventbus.errors

case class UnsupportedMessageTypeException(message: String = "",
                                           cause: Throwable = null)
  extends RuntimeException(message, cause, true, false)
