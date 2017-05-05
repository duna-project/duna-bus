package io.duna.concurrent

import java.util.concurrent.{Executor, ThreadFactory}

import io.netty.util.concurrent.{EventExecutor, MultithreadEventExecutorGroup}

class EventLoopGroup(nThreads: Int,
                     threadFactory: ThreadFactory,
                     args: AnyRef*)
  extends MultithreadEventExecutorGroup(Math.max(1, nThreads), threadFactory, args) {

  def this(nThreads: Int = EventLoopGroup.DEFAULT_EVENT_LOOP_THREADS,
           threadFactory: ThreadFactory = new DunaThreadFactory) =
    this(nThreads, threadFactory, _)

  override def newChild(executor: Executor, args: AnyRef*): EventExecutor =
    new EventLoop(this, executor)

  override def newDefaultThreadFactory(): ThreadFactory = new DunaThreadFactory

}

object EventLoopGroup {

  private val DEFAULT_EVENT_LOOP_THREADS: Int = Math.max(1, Runtime.getRuntime.availableProcessors() * 2)

}
