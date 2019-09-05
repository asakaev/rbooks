package io.github.asakaev.zjs

import org.scalajs.dom.raw.{AudioContext, HTMLAudioElement}
import zio.{Task, ZIO}

object audio {

  val audioContext: Task[AudioContext] =
    ZIO.effect(new AudioContext())

  // TODO: create js.Promise[A] -> Task[A] syntax zjs
  // TODO: maybe no need to use: ac.state == "suspended"
  def resume(ac: AudioContext): Task[Unit] =
    if (ac.state == "suspended") ZIO.fromFuture(_ => ac.resume().toFuture)
    else ZIO.unit

  def play(ae: HTMLAudioElement): Task[Unit] =
    ZIO.effect(ae.play())

  def pause(ae: HTMLAudioElement): Task[Unit] =
    ZIO.effect(ae.pause())

  def playAudio(ac: AudioContext, ae: HTMLAudioElement): Task[Unit] =
    for {
      _ <- resume(ac)
      _ <- play(ae)
    } yield ()

}
