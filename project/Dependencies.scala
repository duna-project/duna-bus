import sbt._

object Dependencies {

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )

  val coreDependencies: Seq[ModuleID] = Seq(
    "org.reactivestreams" % "reactive-streams" % "1.0.0.final",
    "org.jctools" % "jctools-core" % "2.0.1",
    "net.openhft" % "smoothie-map" % "1.3"
  )

  val concurrentDependencies: Seq[ModuleID] = Seq(
    "io.netty" % "netty-common" % "4.1.9.Final",
    "org.jctools" % "jctools-core" % "2.0.1"
  )
}