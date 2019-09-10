package io.github.asakaev.audio

import org.scalajs.dom.raw.AnalyserNode
import zio.ZIO

import scala.scalajs.js.typedarray.Uint8Array

trait Analyzer {
  def writeAnalyserData(buffer: Uint8Array): ZIO[AnalyserNode, Throwable, Unit]
}

object Analyzer extends Analyzer {
  final def writeAnalyserData(buffer: Uint8Array): ZIO[AnalyserNode, Throwable, Unit] =
    ZIO.accessM { analyser =>
      ZIO.effect(analyser.getByteFrequencyData(buffer))
    }
}
