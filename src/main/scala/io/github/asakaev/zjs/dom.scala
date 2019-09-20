package io.github.asakaev.zjs

import org.scalajs.dom.raw.{EventTarget, MouseEvent}
import zio.ZIO
import zio.stream.Stream

object dom {

  // TODO: how to unsubscribe when stream ended by .take(1)
  // TODO: it's possible to have ZStream[HTMLButtonElement, Nothing, MouseEvent]
  def eventStream[A](target: EventTarget, event: String): Stream[Nothing, A] =
    Stream.effectAsync { cb =>
      target.addEventListener(event, (ev: A) => cb(ZIO.succeed(ev)))
    }

  def mouseEvents(target: EventTarget): Stream[Nothing, MouseEvent] =
    eventStream(target, "click")

}
