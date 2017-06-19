import sbt.Keys._
import sbt.{Def, _}

object Dependencies {

  lazy val defaultSettings: Seq[Setting[_]] = Defaults.coreDefaultSettings ++ Seq(
    organization := "io.duna",
    version := "0.1.0-SNAPTHOT",
    scalaVersion := "2.12.2",

    resolvers += "JCenter" at "https://dl.bintray.com/bintray/jcenter"
  )

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )

  val coreDependencies: Seq[ModuleID] = Seq(
    "com.twitter" %% "util-core" % "6.45.0",
    "com.typesafe" % "config" % "1.3.1"
  )

  val concurrentDependencies: Seq[ModuleID] = Seq(
    "io.netty" % "netty-common" % "4.1.12.Final"
  )

  val clusterDependencies: Seq[ModuleID] = Seq(
    "io.netty" % "netty-handler" % "4.1.12.Final",
    "io.netty" % "netty-transport" % "4.1.12.Final"
  )
}