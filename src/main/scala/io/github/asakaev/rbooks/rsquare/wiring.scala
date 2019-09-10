package io.github.asakaev.rbooks.rsquare

import eu.timepit.refined._
import eu.timepit.refined.numeric.NonNegative
import io.github.asakaev.rbooks.rsquare.logic._
import io.github.asakaev.rbooks.rsquare.math._
import io.github.asakaev.rbooks.rsquare.view._
import io.github.asakaev.zjs.dom._
import io.github.asakaev.zjs.graphics.requestedFrameEvents
import org.scalajs.dom.raw.Node
import org.scalajs.dom.svg.Polygon
import zio.ZIO
import zio.console.Console
import zio.stream.ZStream

object wiring {

  def streamApp(n: Node, p: Polygon): ZStream[Console, Throwable, Unit] = {
    val clicks = mouseEvents(n).map(Message.Clicked)
    val ticks  = requestedFrameEvents.map(Message.Ticked)

    // TODO: can not find .unNone for .collect { case Some(v) => v }
    clicks
      .merge(ticks)
      .mapAccumM[Console, Throwable, State, Option[FFT]](State.Idle)(reducer)
      .collect { case Some(m) => m }
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
