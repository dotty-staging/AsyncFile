/*
 *  Ops.scala
 *  (AsyncFile)
 *
 *  Copyright (c) 2020 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Affero General Public License v3+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.asyncfile

import java.net.URI

object Ops {
  implicit final class URIOps(private val uri: URI) extends AnyVal {
    def path: String = uri.getPath

    def name: String = {
      val p = uri.normalize().getPath
      val i = p.lastIndexOf('/') + 1
      p.substring(i)
    }

    def base: String = {
      val p = uri.normalize().getPath
      val i = p.lastIndexOf('/') + 1
      val n = p.substring(i)
      val j = n.lastIndexOf('.')
      if (j < 0) n else n.substring(0, j)
    }

    def extL: String = {
      val p   = uri.normalize().getPath
      val i   = p.lastIndexOf('/') + 1
      val n   = p.substring(i)
      val j   = n.lastIndexOf('.')
      val ext = if (j < 0) "" else n.substring(j + 1)
      // Locale.US -- not available on Scala.js ; rely on user setting JVM's locale appropriately...
      ext.toLowerCase()
    }

    def parentOption: Option[URI] = {
      val p = uri.normalize().getPath
      val j = if (p.endsWith("/")) p.length - 2 else p.length - 1
      val i = p.lastIndexOf('/', j)
      if (i < 0) None else {
        val pp      = if (i == 0) "/" else p.substring(0, i)
        val scheme  = uri.getScheme
        Some(new URI(scheme, pp, null))
      }
    }

    def replaceExt (ext : String): URI = {
      val p   = uri.normalize().getPath
      val i     = p.lastIndexOf('/') + 1
      val n     = p.substring(i)
      val j     = n.lastIndexOf('.')
      val base  = if (j < 0) n else n.substring(0, j)
      val extP  = if (ext.startsWith(".")) ext else "." + ext
      val pNew  = base + extP
      val scheme  = uri.getScheme
      new URI(scheme, pNew, null)
    }

    def replaceName(name: String): URI = {
      val p       = uri.normalize().getPath
      val i       = p.lastIndexOf('/') + 1
      val pNew    = p.substring(0, i) + name
      val scheme  = uri.getScheme
      new URI(scheme, pNew, null)
    }

    def / (sub: String): URI = {
      val parent0 = uri.normalize().getPath
      val parentS = if (parent0.isEmpty || parent0.endsWith("/")) parent0 else s"$parent0/"
      val path    = s"$parentS$sub"
      new URI(uri.getScheme, path, null)
    }
  }
}
