enablePlugins(ScalaJSPlugin)

name := "Reactive Books"
scalaVersion := "2.12.9"
scalaJSUseMainModuleInitializer := true

libraryDependencies ++= List(
  "dev.zio"      %%% "zio"         % "1.0.0-RC12-1",
  "dev.zio"      %%% "zio-streams" % "1.0.0-RC12-1",
  "org.scala-js" %%% "scalajs-dom" % "0.9.7"
)
