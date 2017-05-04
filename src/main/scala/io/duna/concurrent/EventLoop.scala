package io.duna.concurrent

import java.util
import java.util.concurrent._
import java.util.concurrent.atomic.AtomicBoolean

import scala.collection.JavaConverters._
import scala.util.Try

class EventLoop(threadFactory: ThreadFactory) extends ExecutorService {

  private val taskQueue = new LinkedBlockingQueue[Runnable]()

  @volatile private var terminated = false
  private val shuttingDown = new AtomicBoolean(false)

  private val thread = threadFactory.newThread(() => { this.loop() })
  thread.start()

  override def submit[T](task: Callable[T]): Future[T] = {
    if (isShutdown || isTerminated)
      throw new RejectedExecutionException("This event loop is either shutdown or terminated.")

    val future = new CompletableFuture[T]()

    taskQueue.add(() => {
      future complete task.call()
    })

    future
  }

  override def submit[T](task: Runnable, result: T): Future[T] = {
    if (isShutdown || isTerminated)
      throw new RejectedExecutionException("This event loop is either shutdown or terminated.")

    val future = new CompletableFuture[T]()

    taskQueue.add(() => {
      task.run()
      future complete result
    })

    future
  }

  override def submit(task: Runnable): Future[_] = {
    if (isShutdown || isTerminated)
      throw new RejectedExecutionException("This event loop is either shutdown or terminated.")

    val future = new CompletableFuture[Unit]()

    taskQueue.add(() => {
      task.run()
      future.complete(Unit)
    })

    future
  }

  override def isTerminated: Boolean = terminated

  override def invokeAll[T](tasks: util.Collection[_ <: Callable[T]]): util.List[Future[T]] = {
    val (futures, countDownLatch) = doInvokeAll(tasks)

    countDownLatch.await()
    futures
  }

  override def invokeAll[T](tasks: util.Collection[_ <: Callable[T]],
                            timeout: Long, unit: TimeUnit): util.List[Future[T]] = {
    val (futures, countDownLatch) = doInvokeAll(tasks)

    countDownLatch.await(timeout, unit)
    futures
  }

  override def awaitTermination(timeout: Long, unit: TimeUnit): Boolean = ???

  override def invokeAny[T](tasks: util.Collection[_ <: Callable[T]]): T = {
    doInvokeAny(tasks, timed = false, 0)
  }

  override def invokeAny[T](tasks: util.Collection[_ <: Callable[T]], timeout: Long, unit: TimeUnit): T = {
    doInvokeAny(tasks, timed = false, unit.toMillis(timeout))
  }

  override def shutdownNow(): util.List[Runnable] = {
    while (!shuttingDown.compareAndSet(false, true)) {}

    val remainingTasks = new util.ArrayList[Runnable](taskQueue.size())
    taskQueue.drainTo(remainingTasks)

    remainingTasks
  }

  override def shutdown(): Unit = {
    while (!shuttingDown.compareAndSet(false, true)) {}
  }

  override def isShutdown: Boolean = shuttingDown.get

  override def execute(command: Runnable): Unit = {
    this submit command
  }

  private def loop(): Unit = {
    while (!isShutdown) {
      val task = taskQueue.take()
      task.run()
    }

    terminated = true
  }

  private def doInvokeAll[T](tasks: util.Collection[_ <: Callable[T]]): (util.List[Future[T]], CountDownLatch) = {
    if (isShutdown || isTerminated)
      throw new RejectedExecutionException("This event loop is either shutdown or terminated.")

    val futures = new util.ArrayList[Future[T]](tasks.size())
    val countDownLatch = new CountDownLatch(tasks.size())

    tasks.forEach(t => {
      if (t == null)
        throw new NullPointerException()

      futures.add {
        this.submit(() => {
          val result = t.call()
          countDownLatch.countDown()

          result
        })
      }
    })

    (futures, countDownLatch)
  }

  private def doInvokeAny[T](tasks: util.Collection[_ <: Callable[T]], timed: Boolean, deadline: Long): T = {
    if (isShutdown || isTerminated)
      throw new RejectedExecutionException("This event loop is either shutdown or terminated.")

    if (tasks.contains(null))
      throw new NullPointerException

    val completableFuture = new CompletableFuture[T]()
    val startTime = System.currentTimeMillis()

    tasks.forEach(t => {
      if (System.currentTimeMillis() - startTime > deadline) {
        completableFuture.completeExceptionally(new IllegalStateException())
      }

      if (!completableFuture.isDone) {
        val f = this.submit(() => {
          val result = Try(t.call())

          if (!completableFuture.isDone && result.isSuccess)
            completableFuture.complete(result.get)

          result
        })

        if (timed) {
          Try(f.get(deadline, TimeUnit.MILLISECONDS))
        } else {
          Try(f.get())
        }
      }
    })

    completableFuture.get()
  }
}
