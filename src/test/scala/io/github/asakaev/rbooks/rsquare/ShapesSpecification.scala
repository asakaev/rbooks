package io.github.asakaev.rbooks.rsquare

import io.github.asakaev.rbooks.rsquare.shapes.Quadrilateral
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.scalacheck.ScalacheckShapeless._

object ShapesSpecification extends Properties("Shapes") {

  // TODO: fix, floating problem?
  property("scale") = forAll { q: Quadrilateral =>
    q.scale(1) == q
  }

}
