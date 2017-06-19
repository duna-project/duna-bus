import Dependencies._, ProjectSettings._

lazy val `duna-scala` = (project in file("."))
  .settings(defaultSettings: _*)
  .settings(
    resolvers += "Sonatype OSS Snapshots" at
      "https://oss.sonatype.org/content/repositories/snapshots",
    resolvers += "Velvia Maven" at "http://dl.bintray.com/velvia/maven"
  )

lazy val `duna-common` = (project in file("common"))
  .settings(defaultSettings: _*)
  .settings(
    name := "duna-common",
    libraryDependencies ++= coreDependencies ++ testDependencies,

    scalacOptions ++= commonScalacOptions
  )

lazy val `duna-core` = (project in file("core"))
  .settings(defaultSettings: _*)
  .settings(
    name := "duna-core",
    libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % "2.12.2") ++
      Seq("org.scala-lang" % "scala-compiler" % "2.12.2"),
    libraryDependencies ++= coreDependencies ++ concurrentDependencies ++ testDependencies,

    scalacOptions ++= commonScalacOptions
  )
  .dependsOn(`duna-common`)

lazy val `duna-cluster` = (project in file("cluster"))
  .settings(defaultSettings: _*)
  .settings(
    name := "duna-cluster",
    libraryDependencies ++= coreDependencies
      ++ concurrentDependencies
      ++ clusterDependencies
      ++ testDependencies,

    scalacOptions ++= commonScalacOptions
  )
  .dependsOn(`duna-common`, `duna-core`)

lazy val `duna-perf` = (project in file("perf"))
  .settings(defaultSettings: _*)
  .settings(
    name := "duna-perf",
    scalacOptions ++= commonScalacOptions
  )
  .dependsOn(`duna-common`, `duna-core`)
  .enablePlugins(JmhPlugin)