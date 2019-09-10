package io.github.asakaev.audio

import org.scalajs.dom.raw.AudioBufferSourceNode
import zio.ZIO

trait BufferSource {

  /**
    * Only one call to .start() allowed
    */
  def start(offset: Double): ZIO[AudioBufferSourceNode, Throwable, Unit]

  /**
    * Can call .stop() infinite times on looped buffer
    */
  val stop: ZIO[AudioBufferSourceNode, Throwable, Unit]

}

object BufferSource extends BufferSource {

  final def start(offset: Double): ZIO[AudioBufferSourceNode, Throwable, Unit] =
    ZIO.accessM { s =>
      ZIO.effect(s.start(offset = offset))
    }

  final val stop: ZIO[AudioBufferSourceNode, Throwable, Unit] =
    ZIO.accessM { s =>
      ZIO.effect(s.stop())
    }

}
