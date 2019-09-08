package io.github.asakaev.zjs

import org.scalajs.dom.raw.{AudioBuffer, AudioContext}
import zio.{Task, ZIO}

import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

object audio {

  def decodeAudioData(ac: AudioContext, buffer: ArrayBuffer): Task[AudioBuffer] =
    ZIO.effectAsync { cb =>
      ac.decodeAudioData(
        buffer,
        buff => cb(ZIO.succeed(buff)),
        () => cb(ZIO.fail(new Error("decodeAudioData.failed")))
      )
    }

  // TODO: can it be more accurate?
  val audioContext: Task[AudioContext] =
    ZIO.effect {
      if (!js.isUndefined(js.Dynamic.global.AudioContext)) {
        new AudioContext()
      } else {
        new webkitAudioContext()
      }
    }

}
