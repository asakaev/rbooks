package io.github.asakaev.zjs

import org.scalajs.dom.raw.{Event, EventTarget, HTMLAudioElement, MouseEvent}
import zio.ZIO
import zio.stream.Stream

object dom {

  // TODO: it's possible to have ZStream[HTMLButtonElement, Nothing, MouseEvent]
  def mouseEvents(target: EventTarget): Stream[Nothing, MouseEvent] =
    Stream.effectAsync { cb =>
      target.addEventListener("click", { ev: MouseEvent =>
        cb(ZIO.succeed(ev))
      })
    }

  def audioEndedEvents(target: HTMLAudioElement): Stream[Nothing, Event] =
    Stream.effectAsync { cb =>
      target.addEventListener("ended", { ev: Event =>
        cb(ZIO.succeed(ev))
      })
    }

}
