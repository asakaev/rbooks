enablePlugins(ScalaJSPlugin)

name := "Reactive Books"
scalaVersion := "2.13.1"
scalaJSUseMainModuleInitializer := true

libraryDependencies ++= List(
  "dev.zio"                    %%% "zio"                       % "1.0.0-RC17",
  "dev.zio"                    %%% "zio-streams"               % "1.0.0-RC17",
  "org.scala-js"               %%% "scalajs-dom"               % "0.9.8",
  "com.lihaoyi"                %%% "scalatags"                 % "0.8.4",
  "eu.timepit"                 %%% "refined"                   % "0.9.10",
  "org.scalacheck"             %%% "scalacheck"                % "1.14.0" % "test",
  "com.github.alexarchambault" %%% "scalacheck-shapeless_1.14" % "1.2.3" % "test"
)
