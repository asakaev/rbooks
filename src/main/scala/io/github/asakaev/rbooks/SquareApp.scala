package io.github.asakaev.rbooks

import io.github.asakaev.rbooks.rsquare.env._
import io.github.asakaev.zjs._
import zio.console._
import zio.{App, ZIO}

object SquareApp extends App {

  // TODO: add failed IO handling in browser
  def run(args: List[String]) =
    myAppLogic.fold(_ => 1, _ => 0)

  val myAppLogic =
    for {
      _  <- putStrLn(s"Reactive Square")
      _  <- putStrLn(audiElementSrc)
      ac <- audioContext
      ae <- ZIO.fromOption(audioElement)
      _  <- routing(ac, ae)
      _  <- putStrLn("play audio")
      _  <- playAudio(ac, ae)
      _  <- putStrLn("listen button")
      _  <- listenButton
      _  <- putStrLn("*")
    } yield ()
}
