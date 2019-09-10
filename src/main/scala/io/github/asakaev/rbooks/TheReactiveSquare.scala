package io.github.asakaev.rbooks

import io.github.asakaev.rbooks.rsquare.env._
import io.github.asakaev.rbooks.rsquare.view._
import io.github.asakaev.rbooks.rsquare.wiring._
import io.github.asakaev.zjs.graphics._
import org.scalajs.dom.window
import zio.console._
import zio.stream.Sink
import zio.{App, ZIO}

object TheReactiveSquare extends App {

  def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    application
      .tapError(e => putStrLn(e.getMessage))
      .fold(_ => 1, _ => 0)

  val application: ZIO[Environment, Throwable, Unit] =
    for {
      div     <- applicationElement
      appNode <- mountApplication(div, svgQuadrilateral(baseQ))
      poly    <- polygon(appNode)
      _       <- putStrLn(s"The Reactive Square")
      rafs = requestedAnimationFrames.provide(window)
      _ <- streamApp(appNode, poly, rafs).run(Sink.drain)
    } yield ()
}
