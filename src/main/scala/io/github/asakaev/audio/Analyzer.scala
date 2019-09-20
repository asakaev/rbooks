package io.github.asakaev.audio

import org.scalajs.dom.raw.AnalyserNode
import zio.{Ref, ZIO}

import scala.scalajs.js.typedarray.Uint8Array

trait Analyzer {
  val frequencyBinCount: ZIO[AnalyserNode, Throwable, Int]
  def getByteFrequencyData(ref: Ref[Uint8Array]): ZIO[AnalyserNode, Throwable, Unit]
  val fftSize: ZIO[AnalyserNode, Throwable, Int]
  def getByteTimeDomainData(ref: Ref[Uint8Array]): ZIO[AnalyserNode, Throwable, Unit]
}

object Analyzer extends Analyzer {

  final val frequencyBinCount: ZIO[AnalyserNode, Throwable, Int] =
    ZIO.accessM { an =>
      ZIO.effectTotal(an.frequencyBinCount)
    }

  // TODO: should be RefM to allow side effect inside update?

  /**
    * Buffer should be frequencyBinCount size
    */
  final def getByteFrequencyData(ref: Ref[Uint8Array]): ZIO[AnalyserNode, Throwable, Unit] =
    ZIO.accessM { an =>
      for {
        buff <- ref.get
        _    <- ZIO.effect(an.getByteFrequencyData(buff))
      } yield ()
    }

  final val fftSize: ZIO[AnalyserNode, Throwable, Int] =
    ZIO.accessM { an =>
      ZIO.effectTotal(an.fftSize)
    }

  /**
    * Buffer should be fftSize size
    */
  final def getByteTimeDomainData(ref: Ref[Uint8Array]): ZIO[AnalyserNode, Throwable, Unit] =
    ZIO.accessM { an =>
      for {
        buff <- ref.get
        _    <- ZIO.effect(an.getByteTimeDomainData(buff))
      } yield ()
    }
}
