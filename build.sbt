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
  .dependsOn(`duna-common`, `duna-core`, `duna-s11n-api`, `duna-s11n-json4s-native`)

lazy val `duna-perf` = (project in file("perf"))
  .settings(defaultSettings: _*)
  .settings(
    name := "duna-perf",
    scalacOptions ++= commonScalacOptions
  )
  .dependsOn(`duna-common`, `duna-core`)
  .enablePlugins(JmhPlugin)

lazy val `duna-s11n-api` = (project in file("s11n/api"))
  .settings(defaultSettings: _*)
  .settings(
    name := "duna-s11n-api",
    scalacOptions ++= commonScalacOptions
  )
  .dependsOn(`duna-common`)

lazy val `duna-s11n-json4s-native` = (project in file("s11n/json4s-native"))
  .settings(defaultSettings: _*)
  .settings(
    name := "duna-s11n-json4s-native",
    libraryDependencies ++= testDependencies,
    scalacOptions ++= commonScalacOptions
  )
  .dependsOn(`duna-common`, `duna-s11n-api`)