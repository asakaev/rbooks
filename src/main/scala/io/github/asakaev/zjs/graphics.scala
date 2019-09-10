package io.github.asakaev.zjs

import org.scalajs.dom.Window
import zio.ZIO
import zio.stream.ZStream

object graphics {

  // TODO: ZIO.duration

  /**
    * Requests animation frame.
    */
  final val requestAnimationFrame: ZIO[Window, Nothing, Double] =
    ZIO.accessM[Window] { w =>
      ZIO.effectAsync { cb =>
        w.requestAnimationFrame(t => cb(ZIO.succeed(t)))
      }
    }

  final val requestedAnimationFrames: ZStream[Window, Nothing, Double] =
    ZStream.repeatEffect(requestAnimationFrame)

}
