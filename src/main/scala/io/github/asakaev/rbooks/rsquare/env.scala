package io.github.asakaev.rbooks.rsquare

import org.scalajs.dom.document
import org.scalajs.dom.raw._
import org.scalajs.dom.svg.{Polygon, SVG}
import scalatags.JsDom.TypedTag
import zio.{Task, ZIO}

object env {

  val applicationElement: Task[HTMLDivElement] =
    ZIO
      .fromOption(Option(document.getElementById("app").asInstanceOf[HTMLDivElement]))
      .mapError(_ => new Error("HTMLDivElement.empty"))

  def mountApplication(element: HTMLDivElement, svg: TypedTag[SVG]): Task[Node] =
    ZIO.effect(element.appendChild(svg.render))

  def polygon(appNode: Node): Task[Polygon] =
    ZIO.effect(appNode.firstChild.asInstanceOf[Polygon])

}
