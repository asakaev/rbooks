package io.github.asakaev.rbooks.rsquare

import eu.timepit.refined._
import eu.timepit.refined.numeric.NonNegative
import io.github.asakaev.rbooks.rsquare.logic._
import io.github.asakaev.rbooks.rsquare.math._
import io.github.asakaev.rbooks.rsquare.view._
import io.github.asakaev.zjs.dom._
import org.scalajs.dom.raw.Node
import org.scalajs.dom.svg.Polygon
import zio.ZIO
import zio.console._
import zio.stream._

object wiring {

  def streamApp(
      n: Node,
      p: Polygon,
      rafs: Stream[Nothing, Double]
  ): ZStream[Console, Throwable, Unit] = {
    val clicks = mouseEvents(n).map(Message.Clicked)
    val ticks  = rafs.map(Message.Ticked)

    clicks
      .merge(ticks)
      .mapAccumM[Console, Throwable, State, Option[RawData]](State.Idle)(reducer)
      .unNone
        //      .tap(rd => putStrLn(rd.fftBuff.toString))
      .mapM { rawData =>
        ZIO
          .fromEither(refineV[NonNegative](rms(rawData.freqBuff)))
          .bimap(new Error(_), Measure1)
      }
      .mapAccum(RunningState(None))(rmsStatefulReducer)
      .unNone
        //      .tap(m => putStrLn(m.rmsNorm.toString))
      .map(m => viewQuadrilateral(m.rmsNorm))
      .mapM { q =>
        updateQuadrilateral(p.points, q)
      }
  }

}
