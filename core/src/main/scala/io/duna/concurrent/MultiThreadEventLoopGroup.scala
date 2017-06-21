package io.duna.concurrent

import java.util.concurrent.{Executor, ThreadFactory}

import io.netty.util.concurrent.{EventExecutor, MultithreadEventExecutorGroup}

class MultiThreadEventLoopGroup(nThreads: Int,
                                threadFactory: ThreadFactory,
                                args: AnyRef*)
  extends MultithreadEventExecutorGroup(nThreads, threadFactory) {

  override def newChild(executor: Executor, args: AnyRef*): EventExecutor =
    SingleThreadEventLoop(this, threadFactory)
}

object MultiThreadEventLoopGroup {
  def apply(nThreads: Int = Runtime.getRuntime.availableProcessors(),
            threadFactory: ThreadFactory = DunaThreadFactory()): MultiThreadEventLoopGroup =
    new MultiThreadEventLoopGroup(nThreads, threadFactory)

  def apply(nThreads: Int, threadFactory: ThreadFactory, args: AnyRef*): MultiThreadEventLoopGroup =
    new MultiThreadEventLoopGroup(nThreads, threadFactory, args)
}
