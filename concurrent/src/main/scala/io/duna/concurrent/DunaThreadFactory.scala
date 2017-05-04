package io.duna.concurrent

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class DunaThreadFactory extends ThreadFactory {

  private val threadCount = new AtomicInteger(0)

  override def newThread(r: Runnable): Thread = {
    val thread = new DunaThread(s"duna-${threadCount.getAndIncrement()}", r)
    thread.setDaemon(false)

    thread
  }
}
