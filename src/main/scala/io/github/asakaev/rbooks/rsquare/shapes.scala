package io.github.asakaev.rbooks.rsquare

object shapes {

  final case class Point(x: Double, y: Double) {
    def dx(d: Double): Point = Point(x + d, y)
    def dy(d: Double): Point = Point(x, y + d)
  }

  // TODO: Use Square as model, Quadrilateral is for animations only
  final case class Quadrilateral(a: Point, b: Point, c: Point, d: Point) {

    /**
      * `A` as base point
      */
    def scale(factor: Double): Quadrilateral =
      Quadrilateral(
        a,
        b.dx(b.x * factor),
        c.dx(c.x * factor).dy(c.y * factor),
        d.dy(d.y * factor)
      )

    def mkString: String =
      List(a, b, c, d).map(p => s"${p.x},${p.y}").mkString(" ")

  }

}
