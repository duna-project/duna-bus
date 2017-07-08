name := "duna-cluster"

libraryDependencies ++= Seq[ModuleID](
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "io.netty" % "netty-handler" % "4.1.12.Final",
  "io.netty" % "netty-transport" % "4.1.12.Final",
  "com.typesafe" % "config" % "1.3.1"
)

PB.targets in Compile := Seq(
  scalapb.gen(flatPackage = true,
    javaConversions = false,
    grpc = false,
    singleLineToString = false) -> (sourceManaged in Compile).value
)
