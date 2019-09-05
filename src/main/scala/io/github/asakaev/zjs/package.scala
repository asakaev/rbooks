package io.github.asakaev

import org.scalajs.dom.raw.{AudioContext, HTMLAudioElement, HTMLButtonElement, MouseEvent}
import zio.stream.Stream
import zio.{Task, ZIO}

package object zjs {

  // TODO: it's possible to have ZStream[HTMLButtonElement, Nothing, MouseEvent]
  def buttonClickStream(e: HTMLButtonElement): Stream[Nothing, MouseEvent] =
    Stream.effectAsync { cb =>
      e.addEventListener("click", { e: MouseEvent =>
        cb(ZIO.succeed(e))
      })
    }

  // TODO: create js.Promise[A] -> Task[A] syntax zjs
  // maybe should use: ac.state == "suspended"
  def resume(ac: AudioContext): Task[Unit] =
    ZIO.fromFuture(_ => ac.resume().toFuture)

  def play(e: HTMLAudioElement): Task[Unit] =
    ZIO.effect(e.play())

  def playAudio(ac: AudioContext, e: HTMLAudioElement): Task[Unit] =
    for {
      _ <- resume(ac)
      _ <- play(e)
    } yield ()

}
