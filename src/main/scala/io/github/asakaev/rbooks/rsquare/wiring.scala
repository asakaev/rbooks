package io.github.asakaev.rbooks.rsquare

import io.github.asakaev.rbooks.rsquare.logic._
import io.github.asakaev.zjs.dom._
import io.github.asakaev.zjs.graphics.requestedFrameEvents
import org.scalajs.dom.raw.{HTMLAudioElement, Node}
import zio.console.{Console, putStrLn}
import zio.stream.ZStream

object wiring {

  // TODO: ticks always generated even if audio stopped, maybe a performance issue
  def streamApp(ae: HTMLAudioElement, node: Node): ZStream[Console, Throwable, FFT] = {
    val clicks    = mouseEvents(node).map(Message.Clicked)
    val ticks     = requestedFrameEvents.map(Message.Ticked)
    val audioEnds = audioEndedEvents(ae).map(Message.AudioEnded)

    // TODO: can not find .unNone for .collect { case Some(v) => v }
    clicks
      .merge(ticks)
      .merge(audioEnds)
      .mapAccumM[Console, Throwable, State, Option[FFT]](State.Wait())(reducer(ae))
      .collect { case Some(m) => m }
      .tap(fft => putStrLn(fft.toString))
  }

}
