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

  val frames: ZStream[Any, Nothing, Double] =
    ZStream.repeatEffect(requestFrame)

}
