/*
 *  IndexedDBFileSystem.scala
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

object IndexedDBFileSystem extends AsyncFileSystem {
  final val scheme  = "idb"
  final val name    = "IndexedDB File System"

  def openRead(uri: URI)(implicit executionContext: ExecutionContext): Future[AsyncReadableByteChannel] = {
    val _scheme = uri.getScheme
    if (_scheme != scheme) throw new IllegalArgumentException(s"Scheme ${_scheme} is not $scheme")
    val path = uri.getPath
    IndexedDBFile.openRead(path)
  }

  def openWrite(uri: URI, append: Boolean = false)
               (implicit executionContext: ExecutionContext): Future[AsyncWritableByteChannel] = {
    val _scheme = uri.getScheme
    if (_scheme != scheme) throw new IllegalArgumentException(s"Scheme ${_scheme} is not $scheme")
    val path = uri.getPath
    IndexedDBFile.openWrite(path, append = append)
  }
}
