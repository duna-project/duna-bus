name := "duna-core"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "io.netty" % "netty-common" % "4.1.12.Final",
  "io.reactivex.rxjava2" % "rxjava" % "2.1.1"
)