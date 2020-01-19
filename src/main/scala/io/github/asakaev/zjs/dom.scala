package io.github.asakaev.zjs

import org.scalajs.dom.raw.{EventTarget, MouseEvent}
import org.scalajs.dom.{UIEvent, window}
import zio.stream.Stream
import zio.{Task, ZIO}

object dom {

  final case class Screen(height: Int, width: Int)

  // TODO: how to unsubscribe when stream ended by .take(1)
  // TODO: it's possible to have ZStream[HTMLButtonElement, Nothing, MouseEvent]
  def eventStream[A](target: EventTarget, event: String): Stream[Nothing, A] =
    Stream.effectAsync { cb =>
      target.addEventListener(event, (ev: A) => cb(ZIO.succeed(ev)))
    }

  def mouseEvents(target: EventTarget): Stream[Nothing, MouseEvent] =
    eventStream(target, "click")

  val resizeStream: Stream[Nothing, UIEvent] =
    eventStream[UIEvent](window, "resize")

  val screen: Task[Screen] =
    for {
      h <- ZIO.effect(window.innerHeight.toInt)
      w <- ZIO.effect(window.innerWidth.toInt)
    } yield Screen(h, w)

}
