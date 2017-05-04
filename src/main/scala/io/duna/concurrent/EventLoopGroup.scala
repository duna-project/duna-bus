package io.duna.concurrent

import java.util
import java.util.concurrent.{Callable, ExecutorService, Future, TimeUnit}

class EventLoopGroup(nThreads: Int) extends ExecutorService {
  override def submit[T](task: Callable[T]): Future[T] = ???

  override def submit[T](task: Runnable, result: T): Future[T] = ???

  override def submit(task: Runnable): Future[_] = ???

  override def isTerminated: Boolean = ???

  override def invokeAll[T](tasks: util.Collection[_ <: Callable[T]]): util.List[Future[T]] = ???

  override def invokeAll[T](tasks: util.Collection[_ <: Callable[T]], timeout: Long, unit: TimeUnit): util.List[Future[T]] = ???

  override def awaitTermination(timeout: Long, unit: TimeUnit): Boolean = ???

  override def shutdownNow(): util.List[Runnable] = ???

  override def invokeAny[T](tasks: util.Collection[_ <: Callable[T]]): T = ???

  override def invokeAny[T](tasks: util.Collection[_ <: Callable[T]], timeout: Long, unit: TimeUnit): T = ???

  override def shutdown(): Unit = ???

  override def isShutdown: Boolean = ???

  override def execute(command: Runnable): Unit = ???
}
