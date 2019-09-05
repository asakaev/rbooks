enablePlugins(ScalaJSPlugin)

name := "Reactive Books"
scalaVersion := "2.12.9"
scalaJSUseMainModuleInitializer := true
libraryDependencies += "dev.zio" %%% "zio" % "1.0.0-RC12-1"
