package io.github.asakaev.rbooks.rsquare

import org.scalajs.dom.svg.SVG
import scalatags.JsDom._
import scalatags.JsDom.all._
import scalatags.JsDom.svgAttrs.{points, viewBox}
import scalatags.JsDom.svgTags.{polygon, svg}

object view {

  val squareSvg: TypedTag[SVG] =
    svg(
      viewBox := "0 0 200 200",
      xmlns := "http://www.w3.org/2000/svg",
      polygon(points := "0,0 100,0 100,100 0,100")
    )

}
