package io.duna.concurrent

import java.util.concurrent.Executor
import scala.annotation.tailrec

import io.netty.util.concurrent._

class EventLoop(parent: EventExecutorGroup,
                executor: Executor,
                addTaskWakesUp: Boolean = true,
                maxPendingTasks: Int = EventLoop.DEFAULT_MAX_PENDING_TASKS,
                rejectedExecutionHandler: RejectedExecutionHandler = RejectedExecutionHandlers.reject())
  extends SingleThreadEventExecutor(parent, executor, addTaskWakesUp, maxPendingTasks, rejectedExecutionHandler) {

  override def run(): Unit = doRun()

  @tailrec private def doRun(): Unit = {
    val task = takeTask()

    if (task != null) {
      task.run()
      updateLastExecutionTime()
    }

    if (confirmShutdown()) return

    doRun()
  }
}

object EventLoop {

  private val DEFAULT_MAX_PENDING_TASKS: Int = 16

}
