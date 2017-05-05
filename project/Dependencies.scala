import sbt._

object Dependencies {

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )

  val coreDependencies: Seq[ModuleID] = Seq(
    "org.jctools" % "jctools-core" % "2.0.1",
    "co.fs2" %% "fs2-core" % "0.9.5"
  )

  val concurrentDependencies: Seq[ModuleID] = Seq(
    "io.netty" % "netty-common" % "4.1.9.Final"
  )
}