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
    libraryDependencies ++= coreDependencies
      ++ concurrentDependencies
      ++ clusterDependencies
      ++ testDependencies,

    scalacOptions ++= commonScalacOptions
  )
  .dependsOn(`duna-common`)
