package io.github.asakaev.rbooks.rsquare

import io.github.asakaev.zjs._
import org.scalajs.dom.document
import org.scalajs.dom.raw._
import zio.stream._
import zio.{Task, ZIO}

object env {
  val audioContext: Task[AudioContext] =
    ZIO.effect(new AudioContext())

  val audioElement: Option[HTMLAudioElement] = Option(
    document.querySelector("audio").asInstanceOf[HTMLAudioElement]
  )

  val buttonElement: Option[HTMLButtonElement] = Option(
    document.querySelector("button").asInstanceOf[HTMLButtonElement]
  )

  val buttonStream: Stream[Nothing, MouseEvent] =
    buttonElement.map(buttonClickStream).getOrElse(Stream.empty)

  val pointPrinter: ZStream[Any, Nothing, Unit] =
    buttonStream.mapM { e =>
      ZIO.effectTotal(println(s"x:${e.screenX}, y:${e.screenY}"))
    }

  val listenButton: ZIO[Any, Nothing, Unit] = pointPrinter.run(Sink.drain)

  val audiElementSrc: String =
    audioElement.fold("No audio element")(_.src)

  def routing(ac: AudioContext, element: HTMLAudioElement): Task[Unit] = {
    val audioSource: MediaElementAudioSourceNode = ac.createMediaElementSource(element)
    val analyzer: AnalyserNode                   = ac.createAnalyser()

    for {
      _ <- ZIO.effect(audioSource.connect(analyzer))
      _ <- ZIO.effect(analyzer.connect(ac.destination))
    } yield ()
  }

}
