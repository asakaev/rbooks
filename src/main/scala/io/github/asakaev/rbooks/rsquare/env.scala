package io.github.asakaev.rbooks.rsquare

import io.github.asakaev.zjs.dom._
import io.github.asakaev.zjs.graphics._
import org.scalajs.dom.document
import org.scalajs.dom.raw._
import org.scalajs.dom.svg.SVG
import scalatags.JsDom.TypedTag
import zio.clock.Clock
import zio.duration.Duration
import zio.stream._
import zio.{Task, ZIO}

import scala.concurrent.duration._

object env {

  val audioElement: Option[HTMLAudioElement] = Option(
    document.querySelector("audio").asInstanceOf[HTMLAudioElement]
  )

  val applicationElement: Option[HTMLDivElement] =
    Option(document.getElementById("app").asInstanceOf[HTMLDivElement])

  def mountApplication(element: HTMLDivElement, svg: TypedTag[SVG]): Task[Node] =
    ZIO.effect(element.appendChild(svg.render))

  val framesValues: ZStream[Any with Clock, Nothing, Unit] =
    frames
      .throttleShape(1, Duration.fromScala(1.second))(_ => 1)
      .mapM(v => ZIO.effectTotal(println(v)))

  def streams(node: Node): ZIO[Any with Clock, Nothing, Unit] =
    clicks(node)
      .mapM { e =>
        ZIO.effectTotal(println(s"x:${e.screenX}, y:${e.screenY}"))
      }
      .merge(framesValues)
      .run(Sink.drain)

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
