/*
 *  IndexedDBFileSystem.scala
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

import java.io.File
import java.net.URI

import de.sciss.asyncfile.IndexedDBFile.{READ_ONLY, STORES_FILES, STORE_FILES}
import org.scalajs.dom.raw.IDBObjectStore

import scala.collection.immutable.{Seq => ISeq}
import scala.concurrent.{ExecutionContext, Future}

object IndexedDBFileSystem extends AsyncFileSystem {
  final val scheme  = "idb"
  final val name    = "IndexedDB File System"

  def openRead(uri: URI)(implicit executionContext: ExecutionContext): Future[AsyncReadableByteChannel] = {
    val _scheme = uri.getScheme
    if (_scheme != scheme) throw new IllegalArgumentException(s"Scheme ${_scheme} is not $scheme")
    IndexedDBFile.openRead(uri)
  }

  def openWrite(uri: URI, append: Boolean = false)
               (implicit executionContext: ExecutionContext): Future[AsyncWritableByteChannel] = {
    val _scheme = uri.getScheme
    if (_scheme != scheme) throw new IllegalArgumentException(s"Scheme ${_scheme} is not $scheme")
    IndexedDBFile.openWrite(uri, append = append)
  }

  def mkDir(uri: URI)(implicit executionContext: ExecutionContext): Future[Unit] = {
    Future.failed(new NotImplementedError("idb.mkDir"))
  }

  def mkDirs(uri: URI)(implicit executionContext: ExecutionContext): Future[Unit] = {
    Future.failed(new NotImplementedError("idb.mkDirs"))
  }

  def delete(uri: URI)(implicit executionContext: ExecutionContext): Future[Unit] = {
    Future.failed(new NotImplementedError("idb.delete"))
  }

  def info(uri: URI)(implicit executionContext: ExecutionContext): Future[FileInfo] = {
    for {
      db <- IndexedDBFile.openFileSystem()
      tx = db.transaction(STORES_FILES, mode = READ_ONLY)
      meta <- {
        implicit val store: IDBObjectStore = tx.objectStore(STORE_FILES)
        IndexedDBFile.readMeta(uri)
      }
    } yield {
      meta.info
    }
  }

  def listDir(uri: URI)(implicit executionContext: ExecutionContext): Future[ISeq[URI]] = {
    Future.failed(new NotImplementedError("idb.listDir"))
  }
}
