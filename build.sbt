import Dependencies._
import com.typesafe.sbt.packager.MappingsHelper.directory

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      Seq(
        name := "mocky-2020",
        scalaVersion := "2.13.2"
      )),
    resolvers += Resolver.bintrayRepo("tabmo", "maven"),
    libraryDependencies ++= (http4s ++ circe ++ doobie ++ pureconfig ++ log ++ cache ++ scalatest)
    // Allow to temporary disable Fatal-Warning (can be useful during big refactoring)
    //scalacOptions -= "-Xfatal-warnings"
  )
  // Package with resources
  .enablePlugins(JavaAppPackaging)
  .settings(mappings in Universal ++= directory("src/main/resources"))
  // Make build information available at runtime
  .enablePlugins(BuildInfoPlugin)
  .settings(
    Seq(
      buildInfoKeys := Seq[BuildInfoKey](name, version),
      buildInfoOptions += BuildInfoOption.BuildTime
    ))
  // Activate Integration Tests
  .configs(IntegrationTest) // Affect the same settings to integration test module
  .settings(Defaults.itSettings) // Allows to run it:xxx tasks

Global / onChangedBuildSource := ReloadOnSourceChanges

import ReleaseTransformations._

// ...

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions, // : ReleaseStep
  runClean, // : ReleaseStep
  runTest, // : ReleaseStep
  setReleaseVersion, // : ReleaseStep
  commitReleaseVersion, // : ReleaseStep, performs the initial git checks
  tagRelease, // : ReleaseStep
  setNextVersion, // : ReleaseStep
  commitNextVersion, // : ReleaseStep
  pushChanges // : ReleaseStep, also checks that an upstream branch is properly configured
)
