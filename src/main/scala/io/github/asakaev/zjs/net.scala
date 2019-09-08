package io.github.asakaev.zjs

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Uri
import org.scalajs.dom.raw.{ErrorEvent, XMLHttpRequest}
import zio.ZIO

import scala.scalajs.js.typedarray.ArrayBuffer

object net {

  // TODO: check what is ErrorEvent
  // TODO: handle exceptions, 404 etc
  def request(uri: String Refined Uri): ZIO[Any, ErrorEvent, ArrayBuffer] =
    ZIO.effectAsync { cb =>
      val req: XMLHttpRequest = new XMLHttpRequest()
      req.responseType = "arraybuffer"
      req.onload = _ => cb(ZIO.succeed(req.response.asInstanceOf[ArrayBuffer]))
      req.onerror = e => cb(ZIO.fail(e))
      req.open("GET", uri.value)
      req.send()
    }

}
