/*
 *  DesktopFileSystem.scala
 *  (AsyncFile)
 *
 *  Copyright (c) 2020 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.asyncfile

import java.net.URI

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object DesktopFileSystem extends AsyncFileSystem {
  final val scheme  = "file"
  final val name    = "Desktop File System"

  def openRead(uri: URI)(implicit executionContext: ExecutionContext): Future[AsyncReadableByteChannel] = {
    val _scheme = uri.getScheme
    if (_scheme != scheme) throw new IllegalArgumentException(s"Scheme ${_scheme} is not $scheme")
    val path  = uri.getPath
    val tr    = Try(DesktopFile.openRead(path))
    tr match {
      case Success(ch) => Future.successful (ch)
      case Failure(ex) => Future.failed     (ex)
    }
  }

  def openWrite(uri: URI, append: Boolean = false)
               (implicit executionContext: ExecutionContext): Future[AsyncWritableByteChannel] = {
    val _scheme = uri.getScheme
    if (_scheme != scheme) throw new IllegalArgumentException(s"Scheme ${_scheme} is not $scheme")
    val path  = uri.getPath
    val tr    = Try(DesktopFile.openWrite(path, append = append))
    tr match {
      case Success(ch) => Future.successful (ch)
      case Failure(ex) => Future.failed     (ex)
    }
  }
}
