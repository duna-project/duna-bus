object ProjectSettings {
  val commonScalacOptions = Seq(
    "-encoding", "UTF-8" // Specify character encoding used by source files
    , "-target:jvm-1.8" // Target platform for object files
    , "-Xexperimental" // Enable experimental extensions
    , "-Xfuture" // Turn on future language features
  )
}