package io.github.asakaev.rbooks.rsquare

import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.NonNegative
import io.github.asakaev.zjs.audio._
import io.github.asakaev.zjs.net.request
import org.scalajs.dom.html.Audio
import org.scalajs.dom.raw.{
  AnalyserNode,
  AudioBuffer,
  AudioBufferSourceNode,
  AudioContext,
  Event,
  MouseEvent
}
import zio.clock.Clock
import zio.console._
import zio.{Task, UIO, ZIO}

import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array

object logic {

  val conf = Config("audio.mp3")

  def createAudio(): Audio =
    js.Dynamic.newInstance(js.Dynamic.global.Audio)().asInstanceOf[Audio]

  case class AudioState(
      buff: Uint8Array,
      analyser: AnalyserNode,
      source: AudioBufferSourceNode,
      duration: Double
  )

  case class Playback(played: Double, startedAt: Double)

  trait State
  object State {
    final case class Waiting() extends State
    final case class Playing(audioState: AudioState, ctx: AudioContext, playback: Playback)
        extends State
    final case class Paused(ctx: AudioContext, playback: Playback) extends State
  }

  /**
    *  It change context state from Suspended to Running
    */
  def createAnalyser(context: AudioContext, fftSize: Int): Task[AnalyserNode] =
    ZIO.effect {
      val analyser = context.createAnalyser()
      analyser.fftSize = fftSize
      analyser
    }

  def createBuffer(n: Int): UIO[Uint8Array] =
    ZIO.effectTotal(new Uint8Array(n))

  def createSource(context: AudioContext, decoded: AudioBuffer): Task[AudioBufferSourceNode] =
    ZIO.effect {
      val source = context.createBufferSource()
      source.buffer = decoded
      source
    }

  def route(
      context: AudioContext,
      source: AudioBufferSourceNode,
      analyser: AnalyserNode
  ): Task[Unit] =
    ZIO.effect {
      source.connect(analyser)
      analyser.connect(context.destination)
    }

  def runSource(source: AudioBufferSourceNode, offset: Double): Task[Unit] =
    ZIO.effect(source.start(offset = offset))

  // TODO: source.onended can be used to track ending. but this is stream before this reducer
  // TODO: stop should be idempotent, so stop is invalid state is nothing
  // TODO: Fix async playback ending?
  // TODO: isEnded? -> false, *ACTUALLY ENDS NOW*, .stop(), *BOOM*
  def isEnded(played: Double, duration: Double): Boolean =
    played >= duration

  def playedUpdated(pb0: Playback, currentTime: Double): Playback =
    Playback(pb0.played + (currentTime - pb0.startedAt), currentTime)

  def pausedUpdated(pb0: Playback, currentTime: Double): Playback =
    Playback(pb0.played, currentTime)

  def audioStateCreate(ctx: AudioContext): ZIO[Console, Throwable, AudioState] =
    for {
      _         <- putStrLn(s"AC: ${ctx.state}, SR: ${ctx.sampleRate} T: ${ctx.currentTime}")
      _         <- putStrLn("Create analyser")
      analyser  <- createAnalyser(ctx, 32)
      _         <- putStrLn(s"AC: ${ctx.state}, SR: ${ctx.sampleRate} T: ${ctx.currentTime}")
      audioData <- request(conf.audio).mapError(e => new Error(e.message))
      _         <- putStrLn(s"Loaded ${audioData.byteLength} bytes of ${conf.audio}")
      decoded   <- decodeAudioData(ctx, audioData)
      _         <- putStrLn(s"Decoded SR: ${decoded.sampleRate}")
      source    <- createSource(ctx, decoded)
      _         <- route(ctx, source, analyser)
      buff      <- createBuffer(analyser.frequencyBinCount)
      _         <- putStrLn(s"Buffer created: ${buff.toString}")

    } yield AudioState(buff, analyser, source, decoded.duration)

  val reducer: (State, Message) => ZIO[Console with Clock, Throwable, (State, Option[FFT])] = {
    case (State.Waiting(), Message.Clicked(_)) =>
      for {
        ctx <- audioContext
        as  <- audioStateCreate(ctx)
        playback = Playback(0, ctx.currentTime)
        _ <- putStrLn(s"Start: $playback")
        _ <- runSource(as.source, 0)
      } yield State.Playing(as, ctx, playback) -> None
    case (State.Playing(audioState, ctx, pb0), Message.Clicked(_)) =>
      for {
        _ <- putStrLn(s"Total playback time: ${audioState.duration}")
        currentTime = ctx.currentTime
        playback    = playedUpdated(pb0, currentTime)
        _ <- ZIO.effect(audioState.source.stop())
        _ <- putStrLn(s"Paused: $playback")
      } yield State.Paused(ctx, playback) -> None
    case (State.Paused(ctx, pb0), Message.Clicked(_)) =>
      for {
        as <- audioStateCreate(ctx)
        playback = pausedUpdated(pb0, ctx.currentTime)
        _ <- putStrLn(s"Resume: $playback")
        _ <- runSource(as.source, pb0.played)
      } yield State.Playing(as, ctx, playback) -> None
    case (s @ State.Playing(audioState, ctx, pb0), Message.Ticked(ts)) =>
      for {
        _ <- ZIO.effect(audioState.analyser.getByteFrequencyData(audioState.buff))
        playback = playedUpdated(pb0, ctx.currentTime)

        // TODO: replace this hack with proper solution
        res <- if (isEnded(playback.played, audioState.duration)) {
          for {
            _ <- putStrLn(s"Paused with reset played time: $playback")
          } yield State.Paused(ctx, Playback(0, ctx.currentTime)) -> None
        } else {
          ZIO.succeed(s -> Some(FFT(ts, audioState.buff)))
        }
      } yield res
    case (s, _) =>
      // TODO: fix useless ticks
      ZIO.succeed(s -> None)
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
