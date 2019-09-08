package io.github.asakaev.rbooks.rsquare

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Uri

final case class Config(audio: String Refined Uri)
