package io.duna.util

import scala.util.control.NonFatal

import sun.misc.Unsafe

/**
  * Created by eduribeiro on 22/06/2017.
  */
object Unsafe {

  private lazy val unsafe: Unsafe = {
    val field = classOf[Unsafe].getDeclaredField("theUnsafe")
    field.setAccessible(true)
    field.get(null).asInstanceOf[Unsafe]
  }

  def apply(): Unsafe = unsafe
}
