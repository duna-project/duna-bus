import Dependencies._

lazy val `duna-core` = (project in file("."))
  .settings(
    name := "duna-core-scala",
    organization := "io.duna",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.2",

    libraryDependencies ++= coreDependencies
  )