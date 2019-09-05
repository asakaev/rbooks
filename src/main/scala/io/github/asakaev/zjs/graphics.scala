package io.github.asakaev.zjs

import org.scalajs.dom.window.requestAnimationFrame
import zio.stream.ZStream
import zio.{UIO, ZIO}

object graphics {

  val requestFrame: UIO[Double] =
    ZIO.effectAsync { cb =>
      requestAnimationFrame({ v =>
        cb(ZIO.succeed(v))
      })
    }

  // TODO: do not work as expected => .throttleShape(1, 500.millis)(_ => 1)
  val requestedFrameEvents: ZStream[Any, Nothing, Double] =
    ZStream.repeatEffect(requestFrame)

}
