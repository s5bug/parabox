lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    organization := "tf.bug",
    name := "parabox-core",
    version := "0.1.0",
    scalaVersion := "3.1.2",
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
    scalaVersion := "3.1.2",
    scalacOptions ++= Seq(
      "-no-indent",
      "-old-syntax",
    ),

    showCursor := true,
    title := "Parabox",
    gameAssetsDirectory := "src/main/resources",
    windowStartWidth := 1600,
    windowStartHeight := 900,
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "indigo" % "0.12.1",
      "io.indigoengine" %%% "indigo-extras" % "0.12.1",
      "io.indigoengine" %%% "indigo-json-circe" % "0.12.1",
      ("org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0").cross(CrossVersion.for3Use2_13),
    ),
  )
  .dependsOn(coreJS)
