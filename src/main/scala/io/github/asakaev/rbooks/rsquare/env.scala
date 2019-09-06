package io.github.asakaev.rbooks.rsquare

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.GreaterEqual
import org.scalajs.dom.document
import org.scalajs.dom.raw._
import org.scalajs.dom.svg.{Polygon, SVG}
import scalatags.JsDom.TypedTag
import zio.{Task, ZIO}

import scala.scalajs.js.typedarray.Uint8Array

object env {

  val audioElement: Task[HTMLAudioElement] =
    ZIO
      .fromOption(Option(document.querySelector("audio").asInstanceOf[HTMLAudioElement]))
      .mapError(_ => new Error("HTMLAudioElement.empty"))

  val applicationElement: Task[HTMLDivElement] =
    ZIO
      .fromOption(Option(document.getElementById("app").asInstanceOf[HTMLDivElement]))
      .mapError(_ => new Error("HTMLDivElement.empty"))

  def mountApplication(element: HTMLDivElement, svg: TypedTag[SVG]): Task[Node] =
    ZIO.effect(element.appendChild(svg.render))

  def polygon(appNode: Node): Task[Polygon] =
    ZIO.effect(appNode.firstChild.asInstanceOf[Polygon])

  def updateFFTSize(an: AnalyserNode, n: Int Refined GreaterEqual[W.`32`.T]): Task[Unit] =
    ZIO.effect(an.fftSize = n)

  def analyserNode(ac: AudioContext, ae: HTMLAudioElement): Task[AnalyserNode] =
    for {
      source   <- ZIO.effect(ac.createMediaElementSource(ae))
      analyser <- ZIO.effect(ac.createAnalyser())
      _        <- updateFFTSize(analyser, 32)
      _        <- ZIO.effect(source.connect(analyser))
      _        <- ZIO.effect(analyser.connect(ac.destination))
    } yield analyser

  // TODO: memory pressure can be reduced if reuse buffer
  def read(analyser: AnalyserNode): ZIO[Any, Throwable, Uint8Array] = {
    val buffSize: Task[Int] = ZIO.effect(analyser.frequencyBinCount)
    // TODO: Ref?
    def alloc(size: Int): Task[Uint8Array]     = ZIO.effect(new Uint8Array(size))
    def update(buffer: Uint8Array): Task[Unit] = ZIO.effect(analyser.getByteFrequencyData(buffer))

    for {
      n      <- buffSize
      buffer <- alloc(n)
      _      <- update(buffer)
    } yield buffer
  }

}
