package io.github.asakaev.rbooks.rsquare

import eu.timepit.refined._
import eu.timepit.refined.numeric.NonNegative
import io.github.asakaev.rbooks.rsquare.logic._
import io.github.asakaev.rbooks.rsquare.math._
import io.github.asakaev.rbooks.rsquare.view._
import io.github.asakaev.zjs.dom._
import io.github.asakaev.zjs.graphics.requestedFrameEvents
import org.scalajs.dom.raw.{HTMLAudioElement, Node}
import org.scalajs.dom.svg.Polygon
import zio.ZIO
import zio.console.{Console, putStrLn}
import zio.stream.ZStream

object wiring {

  // TODO: ticks always generated even if audio stopped, maybe a performance issue
  def streamApp(ae: HTMLAudioElement, n: Node, p: Polygon): ZStream[Console, Throwable, Unit] = {
    val clicks    = mouseEvents(n).map(Message.Clicked)
    val ticks     = requestedFrameEvents.map(Message.Ticked)
    val audioEnds = audioEndedEvents(ae).map(Message.AudioEnded)

    // TODO: can not find .unNone for .collect { case Some(v) => v }
    clicks
      .merge(ticks)
      .merge(audioEnds)
      .mapAccumM[Console, Throwable, State, Option[FFT]](State.Wait())(reducer(ae))
      .collect { case Some(m) => m }
      .tap(m => putStrLn(s"[${m.buff}]"))
      .mapM { fft =>
        ZIO
          .fromEither(refineV[NonNegative](rms(fft.buff)))
          .bimap(new Error(_), rms => Measure(rms, normalize(rms.value)))
      }
      .map(m => viewQuadrilateral(m.rmsNorm))
      .mapM { q =>
        updateQuadrilateral(p.points, q)
      }
  }

}
