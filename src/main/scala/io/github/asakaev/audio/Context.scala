package io.github.asakaev.audio

import org.scalajs.dom.raw.{AnalyserNode, AudioBuffer, AudioBufferSourceNode, AudioContext}
import zio.ZIO

import scala.scalajs.js.typedarray.ArrayBuffer

// TODO: Use ZIO.duration for time
trait Context {

  /**
    * Current AudioContext time
    */
  val currentTime: ZIO[AudioContext, Nothing, Double]

  /**
    *  It change context state from Suspended to Running
    */
  def createAnalyser(fftSize: Int): ZIO[AudioContext, Throwable, AnalyserNode]

  /**
    *  It's like iterator. Can not be reused/rewind
    *  Should be new after audio ends
    */
  def createSource(buffer: AudioBuffer): ZIO[AudioContext, Throwable, AudioBufferSourceNode]

  def decodeAudioData(buffer: ArrayBuffer): ZIO[AudioContext, Throwable, AudioBuffer]

}

object Context extends Context {

  final val currentTime: ZIO[AudioContext, Nothing, Double] =
    ZIO.access(_.currentTime)

  final def createAnalyser(fftSize: Int): ZIO[AudioContext, Throwable, AnalyserNode] =
    ZIO.accessM { ctx =>
      for {
        analyser <- ZIO.effect(ctx.createAnalyser())
        _        <- ZIO.effect(analyser.fftSize = fftSize)
      } yield analyser
    }

  final def createSource(buffer: AudioBuffer): ZIO[AudioContext, Throwable, AudioBufferSourceNode] =
    ZIO.accessM { ctx =>
      for {
        s <- ZIO.effect(ctx.createBufferSource())
        _ <- ZIO.effect(s.buffer = buffer)
        _ <- ZIO.effect(s.loop = true)
      } yield s
    }

  final def decodeAudioData(buffer: ArrayBuffer): ZIO[AudioContext, Throwable, AudioBuffer] =
    ZIO.accessM { ctx =>
      ZIO.effectAsync { cb =>
        ctx.decodeAudioData(
          buffer,
          decoded => cb(ZIO.succeed(decoded)),
          () => cb(ZIO.fail(new Error("decodeAudioData.failed")))
        )
      }
    }

}
