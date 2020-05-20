lazy val commonSettings = Seq(
  name := "mocky-2020",
  version := "1.0.0-SNAPSHOT",
  scalaVersion := "2.13.2",
  scalacOptions ++= Seq(
    "-deprecation",
    "-Xfatal-warnings",
    "-Ywarn-value-discard",
    "-Xlint:missing-interpolator"
  ),
  resolvers += Resolver.bintrayRepo("tabmo", "maven")
)

lazy val Http4sVersion = "0.21.4"
lazy val DoobieVersion = "0.9.0"
lazy val FlywayVersion = "6.4.2"
lazy val CirceVersion = "0.13.0"
lazy val PureConfigVersion = "0.12.3"
lazy val LogbackVersion = "1.2.3"
lazy val ScalaTestVersion = "3.1.2"
lazy val ScalaMockVersion = "4.4.0"
lazy val PostgresqlVersion = "42.2.12"
lazy val TestContainerVersion = "0.37.0"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion % "it,test",
      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres-circe" % DoobieVersion,
      "org.postgresql" % "postgresql" % PostgresqlVersion,
      "org.flywaydb" % "flyway-core" % FlywayVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-literal" % CirceVersion % "it,test",
      "io.circe" %% "circe-optics" % CirceVersion % "it",
      "io.tabmo" %% "circe-validation-core" % "0.1.0",
      "io.tabmo" %% "circe-validation-extra-rules" % "0.1.0",
      "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.github.blemale" %% "scaffeine" % "3.1.0" % "compile",
      "org.scalatest" %% "scalatest" % ScalaTestVersion % "it,test",
      "org.scalamock" %% "scalamock" % ScalaMockVersion % "test",
      "com.dimafeng" %% "testcontainers-scala-scalatest" % TestContainerVersion % "it",
      "com.dimafeng" %% "testcontainers-scala-postgresql" % TestContainerVersion % "it"
    )
  )

// Export Build-info
lazy val BuildInfo =
  Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version),
    buildInfoOptions += BuildInfoOption.BuildTime
  )

// Disable scaladoc generation
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false
