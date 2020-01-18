package io.github.asakaev.rbooks.rsquare

import io.github.asakaev.rbooks.rsquare.shapes.{Point, Quadrilateral}
import org.scalajs.dom.raw.{SVGPoint, SVGPointList}
import org.scalajs.dom.svg.SVG
import scalatags.JsDom._
import scalatags.JsDom.all._
import scalatags.JsDom.svgAttrs.{points, viewBox}
import scalatags.JsDom.svgTags.{polygon, svg}
import zio.{Task, ZIO}

object view {

  val baseQ: Quadrilateral =
    Quadrilateral(Point(0, 0), Point(100, 0), Point(100, 100), Point(0, 100))

  def viewQuadrilateral(rmsNorm: Double): Quadrilateral =
    baseQ.scale(rmsNorm)

  def svgQuadrilateral(q: Quadrilateral): TypedTag[SVG] =
    svg(
      viewBox := s"0 0 ${q.c.x * 2} ${q.c.y * 2}",
      xmlns := "http://www.w3.org/2000/svg",
      polygon(points := q.mkString)
    )

  def update(sp: SVGPoint, p: Point): ZIO[Any, Nothing, Unit] =
    for {
      _ <- ZIO.succeed(sp.x = p.x)
      _ <- ZIO.succeed(sp.y = p.y)
    } yield ()

  def point(xs: SVGPointList, idx: Int): Task[SVGPoint] =
    ZIO.effect(xs.getItem(idx))

  def updateQuadrilateral(xs: SVGPointList, q: Quadrilateral): Task[Unit] =
    ZIO.traverse_((q.a :: q.b :: q.c :: q.d :: Nil).zipWithIndex) {
      case (qp, idx) =>
        for {
          p <- point(xs, idx)
          _ <- update(p, qp)
        } yield ()
    }

}
