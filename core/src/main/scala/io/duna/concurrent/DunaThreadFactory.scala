package io.duna.concurrent

import java.util.concurrent.ThreadFactory

class DunaThreadFactory extends ThreadFactory {
  override def newThread(r: Runnable): Thread = new Thread(r)
}

object DunaThreadFactory {
  def apply(): DunaThreadFactory = new DunaThreadFactory
}
