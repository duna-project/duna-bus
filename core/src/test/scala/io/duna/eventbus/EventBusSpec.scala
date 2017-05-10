package io.duna.eventbus

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{Executors, ForkJoinPool}

import scala.concurrent.{Future, Promise}
import scala.util.Try

import org.scalatest._
import io.duna.concurrent.eventExecutionContext
import io.duna.eventbus.errors.NoRouteFoundException
import org.scalatest.concurrent.Waiters
import org.scalatest.concurrent.Waiters.Waiter
import org.scalatest.time.SpanSugar._

class EventBusSpec extends FlatSpec
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with Waiters {

  val node: LocalEventBus = new LocalEventBus(Executors.newFixedThreadPool(1))

  behavior of "EventBus"

  it must "route an event emitted to the correct listener" in {
    val w = new Waiter

    node listenTo[String] "test" onReceive { m =>
      w { assert(m.isDefined) }
      w { assertResult("attachment")(m.get) }
      w.dismiss()
    }

    node emit "test" dispatch Some("attachment")

    w await timeout(300.millis)
  }

  it must "reject a message with the wrong type of attachment" in {
    val w = new Waiter

    node listenTo[Int] "test" onError { _ =>
      w.dismiss()
    }

    node emit[String] "test" dispatch Some("attachment")

    w await timeout(300.millis)
  }

  it must "emit an event and listen for a response" in {
    val w = new Waiter

    import dsl._

    node listenTo[Int] "test" onReceive { _ =>
      replyWith(Some("response"))
    }

    node.emit[Int]("test").
      expect[String](reply).
      dispatch(Some(1)).
      onReceive(_ => w.dismiss())

    w await timeout(300.millis)
  }

  it should "cache messages when a listener is present, but without handlers" in {
    val w = new Waiter

    val listener = node listenTo[String] "test"
    node emit "test" dispatch Some("attachment")

    listener onReceive { m =>
      w.dismiss()
    }

    w await timeout(300.millis)
  }

  it must "remove the listener if it is a disposable one" in {
    val w = new Waiter
    val count = new AtomicInteger(0)

    node listenOnceTo[String] "test" onReceive { _ =>
      w { assert(count.incrementAndGet() <= 1) }
    }

    node onError { _ =>
      w.dismiss()
    }

    node emit[String] "test" dispatch()
    node emit[String] "test" dispatch()

    w await timeout(300.millis)
  }

  it must "route an event to all registered listeners" in {
    val w = new Waiter

    node listenTo "test" onReceive { () => w.dismiss() }
    node listenTo "test" onReceive { () => w.dismiss() }

    node <~ "test" dispatch None

    w.await(timeout(300.millis), dismissals(2))
  }

  it must "remove a listener when requested" in {
    val w = new Waiter

    val listener = node ~> "test" onReceive { () =>
      w { fail("shouldn't receive an event") }
    }

    node -= listener

    node onError { _ => w dismiss() }

    (node <~ "test") ! None

    w await timeout(300.millis)
  }

  it should "dispatch exceptions thrown while running the message handler to the error handler" in {
    val w = new Waiter

    node ~> "test" onReceive { () =>
      throw new RuntimeException
    } onError { _ => w.dismiss() }

    (node <~ "test") ! None

    w await timeout(300.millis)
  }

  override protected def beforeAll(): Unit = node.start()

  override protected def beforeEach(): Unit = node.removeAll("test")

  override protected def afterAll(): Unit = node.shutdown()
}
