import sbt._

object Dependencies {

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )

  val coreDependencies: Seq[ModuleID] = Seq(
    "org.scala-lang" % "scala-reflect" % "2.12.2",
    "org.jctools" % "jctools-core" % "2.0.1"
  )

  val concurrentDependencies: Seq[ModuleID] = Seq(
    "io.netty" % "netty-common" % "4.1.9.Final"
  )
}