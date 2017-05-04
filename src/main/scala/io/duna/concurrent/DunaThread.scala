package io.duna.concurrent

class DunaThread(name: String, task: Runnable) extends Thread(task, name) {
  def this(name: String, task: => Unit) = this(name, () => { task })
}
