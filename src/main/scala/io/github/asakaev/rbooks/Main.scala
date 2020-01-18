package io.github.asakaev.rbooks

import io.github.asakaev.rbooks.rsquare.env._
import io.github.asakaev.rbooks.rsquare.view._
import io.github.asakaev.rbooks.rsquare.wiring._
import io.github.asakaev.zjs.graphics._
import org.scalajs.dom.window
import zio._
import zio.console._

object Main extends App {

  def run(args: List[String]): URIO[Console, Int] =
    application
      .tapError(e => putStrLn(e.getMessage))
      .fold(_ => 1, _ => 0)

  val application: ZIO[Console, Throwable, Unit] =
    for {
      div     <- applicationElement
      appNode <- mountApplication(div, svgQuadrilateral(baseQ))
      poly    <- polygon(appNode)
      _       <- putStrLn(s"The Reactive Square")
      rafs = requestedAnimationFrames.provide(window)
      _ <- streamApp(appNode, poly, rafs).runDrain
    } yield ()
}
