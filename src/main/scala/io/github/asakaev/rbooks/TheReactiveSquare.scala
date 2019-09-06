package io.github.asakaev.rbooks

import io.github.asakaev.rbooks.rsquare.env._
import io.github.asakaev.rbooks.rsquare.view._
import io.github.asakaev.rbooks.rsquare.wiring._
import zio.console._
import zio.stream.Sink
import zio.{App, ZIO}

object TheReactiveSquare extends App {

  // TODO: add failed IO handling in browser
  def run(args: List[String]): ZIO[Console, Nothing, Int] =
    application.fold(_ => 1, _ => 0)

  val application: ZIO[Console, Any, Unit] =
    for {
      div     <- applicationElement
      ae      <- audioElement
      appNode <- mountApplication(div, svgQuadrilateral(baseQ))
      poly    <- polygon(appNode)
      _       <- putStrLn(s"The Reactive Square")
      _       <- streamApp(ae, appNode, poly).run(Sink.drain)
    } yield ()
}
