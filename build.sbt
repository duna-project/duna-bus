import Dependencies._

lazy val `duna-scala` = project in file(".")

lazy val `duna-core` = (project in file("core"))
  .settings(
    name := "duna-core",
    organization := "io.duna",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.2",

    libraryDependencies ++= coreDependencies ++ testDependencies
  )
  .dependsOn(`duna-concurrent`)

lazy val `duna-concurrent` = (project in file("concurrent"))
  .settings(
    name := "duna-concurrent",
    organization := "io.duna",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.2",

    libraryDependencies ++= concurrentDependencies ++ testDependencies
  )