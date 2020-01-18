package io.github.asakaev.rbooks.rsquare

import eu.timepit.refined._
import eu.timepit.refined.numeric.NonNegative
import io.github.asakaev.rbooks.rsquare.logic.Message.Resized
import io.github.asakaev.rbooks.rsquare.logic._
import io.github.asakaev.rbooks.rsquare.math._
import io.github.asakaev.rbooks.rsquare.view._
import io.github.asakaev.zjs.dom._
import org.scalajs.dom.raw.{Node, Window}
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
    val clicked = mouseEvents(n).map(Message.Clicked)
    val ticked  = rafs.map(Message.Ticked)

    val initScreen: ZStream[Any, Throwable, Resized] =
      Stream.fromEffect(screen).map(s => Resized(0, s))

    val resizedEvents =
      resizeStream.mapM { ev =>
        val w = ev.target.asInstanceOf[Window]
        for {
          h <- ZIO.effect(w.innerHeight.toInt)
          w <- ZIO.effect(w.innerWidth.toInt)
        } yield Resized(ev.timeStamp, Screen(h, w))
      }

    val resized: ZStream[Console, Throwable, Resized] =
      (initScreen ++ resizedEvents).tap(r => putStrLn(s"* $r"))

    // TODO: resized are buffered but not pulled
    val joined = resized.merge(clicked).merge(ticked)

    joined
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
