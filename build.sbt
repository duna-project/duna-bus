
lazy val `duna-scala` = (project in file("."))
  .aggregate(
    `duna-common`,
    `duna-core`,
    `duna-cluster`,
    `duna-streams`,
    `duna-s11n`
  )

lazy val `duna-common` = project in file("common")

lazy val `duna-core` = (project in file("core"))
  .dependsOn(`duna-common`)

lazy val `duna-cluster` = (project in file("cluster"))
  .dependsOn(`duna-common`, `duna-core`, `duna-s11n`)

lazy val `duna-streams` = (project in file("streams"))
  .dependsOn(`duna-core`)

lazy val `duna-s11n` = (project in file("s11n"))
  .dependsOn(`duna-common`)

lazy val `duna-perf` = (project in file("perf"))
  .dependsOn(`duna-common`, `duna-core`)
  .enablePlugins(JmhPlugin)
