import indigoplugin._

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    organization := "tf.bug",
    name := "parabox-core",
    version := "0.1.0",
    scalaVersion := "3.6.3",
    scalacOptions ++= Seq(
      "-no-indent",
      "-old-syntax",
    ),
  )

lazy val coreJVM = core.jvm
lazy val coreJS = core.js

lazy val game = project
  .enablePlugins(ScalaJSPlugin, SbtIndigo)
  .in(file("game"))
  .settings(
    organization := "tf.bug",
    name := "parabox-game",
    version := "0.1.0",
    scalaVersion := "3.6.3",
    scalacOptions ++= Seq(
      "-no-indent",
      "-old-syntax",
    ),

    indigoOptions := IndigoOptions.defaults
      .cursorVisible
      .withTitle("Parabox")
      .withAssetDirectory((Compile / resourceDirectory).value.relativeTo(baseDirectory.value / "..").get.getPath)
      .withWindowSize(1600, 900),
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "indigo" % "0.18.0",
      "io.indigoengine" %%% "indigo-extras" % "0.18.0",
      "io.indigoengine" %%% "indigo-json-circe" % "0.18.0",
      ("org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0").cross(CrossVersion.for3Use2_13),
    ),
  )
  .dependsOn(coreJS)
