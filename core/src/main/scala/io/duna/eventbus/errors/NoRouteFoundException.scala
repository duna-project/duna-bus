package io.duna.eventbus.errors

case class NoRouteFoundException(message: String,
                            cause: Throwable = null)
  extends RuntimeException(message, cause, true, false)
