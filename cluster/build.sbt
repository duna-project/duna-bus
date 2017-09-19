name := "duna-cluster"

libraryDependencies ++= Seq[ModuleID](
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "io.netty" % "netty-handler" % "4.1.12.Final",
  "io.netty" % "netty-transport" % "4.1.12.Final",
  "com.typesafe" % "config" % "1.3.1"
)
