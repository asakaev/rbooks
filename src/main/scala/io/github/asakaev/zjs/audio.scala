package io.github.asakaev.zjs

import org.scalajs.dom.raw.AudioContext
import zio.{Task, ZIO}

import scala.scalajs.js

object audio {

  // TODO: can it be more accurate?
  val audioContext: Task[AudioContext] = (
    !js.isUndefined(js.Dynamic.global.AudioContext),
    !js.isUndefined(js.Dynamic.global.webkitAudioContext)
  ) match {
    case (true, _) => ZIO.effect(new AudioContext())
    case (_, true) => ZIO.effect(new webkitAudioContext())
    case _         => ZIO.fail(new Error("AudioContext.empty"))
  }

}
