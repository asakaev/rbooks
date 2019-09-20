package io.github.asakaev.rbooks.rsquare

import java.lang.Math.{pow, sqrt}

import scala.scalajs.js.typedarray.Uint8Array

object math {

  // TODO: maybe add ticker that will down max value when no activity
  // TODO: read about compressors etc

  // TODO: Refined NonNegative
  def rms(xs: Uint8Array): Double =
    sqrt(xs.foldLeft(0.0)(_ + pow(_, 2)) / xs.length)

  // TODO: Refined Interval[0, 1]
  // TODO: max Refined non negative
  def normalize(rms: Double, max: Double): Double =
    rms / max

}
