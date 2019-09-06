package io.github.asakaev.rbooks.rsquare

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.NonNegative
import io.github.asakaev.rbooks.rsquare.env._
import io.github.asakaev.zjs.audio._
import org.scalajs.dom.raw.{AnalyserNode, AudioContext, Event, HTMLAudioElement, MouseEvent}
import zio.console._
import zio.{Task, ZIO}

import scala.scalajs.js.typedarray.Uint8Array

object logic {

  trait State
  object State {
    final case class Wait()                                      extends State
    final case class Play(ac: AudioContext, an: AnalyserNode)    extends State
    final case class Suspend(ac: AudioContext, an: AnalyserNode) extends State
  }

  def reducer(
      ae: HTMLAudioElement
  ): (State, Message) => ZIO[Console, Throwable, (State, Option[FFT])] = {
    case (State.Wait(), Message.Clicked(_)) =>
      for {
        ac <- audioContext
        _  <- putStrLn(s"Play ${ae.src}")
        _  <- playAudio(ac, ae)
        an <- analyserNode(ac, ae)
      } yield State.Play(ac, an) -> None
    case (State.Play(ac, an), Message.Clicked(_)) =>
      for {
        _ <- pause(ae)
      } yield State.Suspend(ac, an) -> None
    case (State.Suspend(ac, an), Message.Clicked(_)) =>
      for {
        _ <- playAudio(ac, ae)
      } yield State.Play(ac, an) -> None
    case (s @ State.Play(_, an), Message.Ticked(ts)) =>
      for {
        buff <- read(an)
      } yield s -> Some(FFT(ts, buff))
    case (State.Play(ac, an), Message.AudioEnded(_)) =>
      Task.succeed(State.Suspend(ac, an) -> None)
    case (s, _) =>
      Task.succeed(s -> None)
  }

  trait Message
  object Message {
    final case class Clicked(ev: MouseEvent)   extends Message
    final case class Ticked(timestamp: Double) extends Message
    final case class AudioEnded(ev: Event)     extends Message
  }

  final case class FFT(timestamp: Double, buff: Uint8Array)

  final case class Measure(rms: Double Refined NonNegative, rmsNorm: Double)

}
