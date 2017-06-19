package io.duna.eventbus.message

import scala.reflect.runtime.universe._
import java.util.concurrent.ConcurrentHashMap

object MessageTypePool {

  private val pool = new ConcurrentHashMap[Int, TypeTag[_]]
}
