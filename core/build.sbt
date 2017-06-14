
PB.targets in Compile := Seq(
  scalapb.gen(flatPackage = true,
    javaConversions = false,
    grpc = false,
    singleLineToString = false) -> (sourceManaged in Compile).value
)
