package io.duna

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

package object concurrent {
  implicit lazy val eventExecutionContext: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(new EventLoopGroup())
}
