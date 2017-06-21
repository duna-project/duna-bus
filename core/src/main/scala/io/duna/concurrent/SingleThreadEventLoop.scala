package io.duna.concurrent

import java.util.concurrent._

import scala.annotation.tailrec

import io.netty.util.concurrent.{EventExecutorGroup, SingleThreadEventExecutor}

class SingleThreadEventLoop(parent: EventExecutorGroup,
                            threadFactory: ThreadFactory)
  extends SingleThreadEventExecutor(parent, threadFactory, true) {



  @tailrec
  override final def run(): Unit = {
    val task = pollTask()
    if (task != null) {
      task.run()
      updateLastExecutionTime()
    }

    if (!confirmShutdown())
      run()
  }
}

object SingleThreadEventLoop {
   def apply(parent: EventExecutorGroup, threadFactory: ThreadFactory) =
     new SingleThreadEventLoop(parent, threadFactory)
}
