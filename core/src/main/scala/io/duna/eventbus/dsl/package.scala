package io.duna.eventbus

import scala.reflect.ClassTag

package object dsl {

  object isError {}

  object reply {}

  def replyWith[T: ClassTag](attachment: Option[T]): Unit = {
    Context().owner
      .emit[T](Context().respondTo)
      .dispatch(attachment)
  }
}
