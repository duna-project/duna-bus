import sbt._

object Dependencies {
  val coreDependencies = Seq(
    "org.scala-lang" % "scala-reflect" % "2.12.2",
//    "io.netty" % "netty-common" % "4.1.9.Final",
    "org.jctools" % "jctools-core" % "2.0.1",

    "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )
}