package io.github.asakaev.audio

import org.scalajs.dom.raw.AnalyserNode
import zio.{Ref, ZIO}

import scala.scalajs.js.typedarray.Uint8Array

object Analyzer {

  final def updateFrequency(buffer: Ref[Uint8Array]): ZIO[AnalyserNode, Throwable, Unit] =
    ZIO.accessM { analyser =>
      buffer.modify { buff =>
        analyser.getByteFrequencyData(buff)
        () -> buff
      }
    }
}
