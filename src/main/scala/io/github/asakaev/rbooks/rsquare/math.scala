package io.github.asakaev.rbooks.rsquare

import java.lang.Math.{pow, sqrt}

import scala.scalajs.js.typedarray.Uint8Array

object math {

  // TODO: can be dynamic. scan maximum value in stream and use it
  // TODO: maybe add ticker that will down max value when no activity
  // TODO: read about compressors etc
  // TODO: ad-hoc calibrated
  val MaxRMS = 156.88451166383507

  // TODO: Refined NonNegative
  def rms(xs: Uint8Array): Double =
    sqrt(xs.foldLeft(0.0)(_ + pow(_, 2)) / xs.length)

  // TODO: Refined Interval[0, 1]
  def normalize(rms: Double): Double =
    rms / MaxRMS

}
