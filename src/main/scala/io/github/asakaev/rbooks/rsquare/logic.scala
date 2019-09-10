package io.github.asakaev.rbooks.rsquare

import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.NonNegative
import io.github.asakaev.audio.Analyzer._
import io.github.asakaev.audio.BufferSource._
import io.github.asakaev.audio.Context._
import io.github.asakaev.zjs.audio._
import io.github.asakaev.zjs.net.request
import org.scalajs.dom.raw.{
  AnalyserNode,
  AudioBuffer,
  AudioBufferSourceNode,
  AudioContext,
  MouseEvent
}
import zio.console._
import zio.{Ref, Task, UIO, ZIO}

import scala.scalajs.js.typedarray.Uint8Array

object logic {

  trait Message
  object Message {
    final case class Clicked(ev: MouseEvent)   extends Message
    final case class Ticked(timestamp: Double) extends Message
  }

  case class AudioState(analyser: AnalyserNode, decoded: AudioBuffer, buff: Ref[Uint8Array])

  trait State
  object State {
    final case object Idle extends State
    final case class Playing(
        ctx: AudioContext,
        s: AudioBufferSourceNode,
        audioState: AudioState,
        playback: Playback
    ) extends State
    final case class Paused(ctx: AudioContext, playback: Playback, audioState: AudioState)
        extends State
  }

  case class Playback(offset: Double, startedAt: Double, duration: Double) {
    def updated(t: Double): Playback = Playback((offset + t - startedAt) % duration, t, duration)
  }

  final case class FFT(timestamp: Double, buff: Uint8Array)
  final case class Measure(rms: Double Refined NonNegative, rmsNorm: Double)

  val conf = Config("audio.mp3")

  // TODO: ctx suspend resume on play pause

  val reducer: (State, Message) => ZIO[Console, Throwable, (State, Option[FFT])] = {
    case (State.Idle, Message.Clicked(_)) =>
      for {
        ctx        <- audioContext
        audioState <- createAudioState(ctx)
        s          <- createConnectedBufferSource(ctx, audioState.decoded, audioState.analyser)
        _          <- start(0).provide(s)
        t          <- currentTime.provide(ctx)
        pb = Playback(t, t, audioState.decoded.duration)
        _ <- putStrLn(s"Start: $pb")
      } yield State.Playing(ctx, s, audioState, pb) -> None
    case (State.Playing(ctx, s, audioState, playback), Message.Clicked(_)) =>
      for {
        _ <- stop.provide(s)
        t <- currentTime.provide(ctx)
        pb = playback.updated(t)
        _ <- putStrLn(s"Paused: $pb")
      } yield State.Paused(ctx, pb, audioState) -> None
    case (State.Paused(ctx, playback, audioState), Message.Clicked(_)) =>
      for {
        _ <- putStrLn("Resume")
        s <- createConnectedBufferSource(ctx, audioState.decoded, audioState.analyser)
        t <- currentTime.provide(ctx)
        pb = playback.updated(t)
        _ <- start(pb.offset).provide(s)
      } yield State.Playing(ctx, s, audioState, playback) -> None
    case (s @ State.Playing(_, _, audioState, _), Message.Ticked(ts)) =>
      for {
        _    <- updateFrequency(audioState.buff).provide(audioState.analyser)
        buff <- audioState.buff.get
      } yield s -> Some(FFT(ts, buff))
    case (s, _) =>
      // TODO: fix useless ticks
      ZIO.succeed(s -> None)
  }

  def createBuffer(n: Int): UIO[Uint8Array] =
    ZIO.effectTotal(new Uint8Array(n))

  // TODO: maybe no need to create Source -> Analyser -> Sink graph and Fan-Out is fine
  def connectSource(
      ctx: AudioContext,
      analyser: AnalyserNode,
      s: AudioBufferSourceNode
  ): Task[Unit] =
    for {
      _ <- ZIO.effect(s.connect(analyser))
      _ <- ZIO.effect(analyser.connect(ctx.destination))
    } yield ()

  def createAudioState(ctx: AudioContext): ZIO[Console, Throwable, AudioState] =
    for {
      _ <- putStrLn(s"Ctx: ${ctx.state}, SR: ${ctx.sampleRate} T: ${ctx.currentTime}")
      _ <- putStrLn("Create analyser")
      // TODO: why analyser stops working if create if after request?
      analyser    <- createAnalyser(32).provide(ctx)
      _           <- putStrLn(s"Ctx: ${ctx.state}, SR: ${ctx.sampleRate} T: ${ctx.currentTime}")
      audioBuffer <- request(conf.audio).mapError(e => new Error(e.message))
      _           <- putStrLn(s"Loaded ${audioBuffer.byteLength} bytes of ${conf.audio}")
      decoded     <- decodeAudioData(audioBuffer).provide(ctx)
      _           <- putStrLn(s"Decoded duration: ${decoded.duration}")
      buff        <- createBuffer(analyser.frequencyBinCount)
      rb          <- Ref.make(buff)
      _           <- putStrLn(s"FFT buffer created: [${buff.toString}]")
    } yield AudioState(analyser, decoded, rb)

  def createConnectedBufferSource(
      ctx: AudioContext,
      decoded: AudioBuffer,
      analyser: AnalyserNode
  ): ZIO[Console, Throwable, AudioBufferSourceNode] =
    for {
      _ <- putStrLn("Creating a new AudioBufferSourceNode")
      s <- createSource(decoded).provide(ctx)
      _ <- connectSource(ctx, analyser, s)
    } yield s

}
