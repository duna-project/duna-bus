import sbt.Keys._
import sbt.{AutoPlugin, Def, Defaults, PluginTrigger}
import sbt._

object CommonSettingsPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Defaults.coreDefaultSettings ++ Seq(
    organization := "io.duna",
    version := "0.1.0-SNAPTHOT",
    scalaVersion := "2.12.2",

    scalacOptions := Seq("-encoding", "UTF-8"
      , "-target:jvm-1.8"
      , "-Xexperimental"
      , "-deprecation"
    ),

    resolvers ++= Seq("JCenter" at "https://dl.bintray.com/bintray/jcenter",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Velvia Maven" at "http://dl.bintray.com/velvia/maven"),

    libraryDependencies += ("org.scalatest" %% "scalatest" % "3.0.1" % Test)
  )
}
