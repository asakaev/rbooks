package io.github.asakaev.zjs

import org.scalajs.dom.raw.{EventTarget, MouseEvent}
import zio.ZIO
import zio.stream.Stream

object dom {

  // TODO: it's possible to have ZStream[HTMLButtonElement, Nothing, MouseEvent]
  def clicks(target: EventTarget): Stream[Nothing, MouseEvent] =
    Stream.effectAsync { cb =>
      target.addEventListener("click", { e: MouseEvent =>
        cb(ZIO.succeed(e))
      })
    }

}
