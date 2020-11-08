/*
 *  AsyncFile.scala
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

import java.io.IOException
import java.net.URI

import de.sciss.log.{Level, Logger}

import scala.concurrent.{ExecutionContext, Future}

trait AsyncFileCompanion {
  private var fileSystemMap: Map[String, AsyncFileSystem] = Map.empty

  def fileSystems: Iterable[AsyncFileSystem] = fileSystemMap.values

  def addFileSystem(fs: AsyncFileSystem): Unit =
    fileSystemMap += (fs.scheme -> fs)

  def getFileSystem(scheme: String): Option[AsyncFileSystem] =
    if (scheme == null) None else fileSystemMap.get(scheme)

  def getFileSystem(uri: URI): Option[AsyncFileSystem] =
    getFileSystem(uri.getScheme)

  def openRead(uri: URI)(implicit executionContext: ExecutionContext): Future[AsyncReadableByteChannel] = {
    val scheme  = uri.getScheme
    val fs      = getFileSystem(scheme)
      .getOrElse(throw new UnsupportedFileSystemException(uri))
    fs.openRead(uri)
  }

  def openWrite(uri: URI)(implicit executionContext: ExecutionContext): Future[AsyncWritableByteChannel] = {
    val scheme  = uri.getScheme
    val fs      = getFileSystem(scheme)
      .getOrElse(throw new UnsupportedFileSystemException(uri))
    fs.openWrite(uri)
  }

  private final val PROP_LOG_LEVEL = "de.sciss.asyncfile.log-level"

  val log: Logger = new Logger("AsyncFile",
    try {
      sys.props.getOrElse(PROP_LOG_LEVEL, null).toInt
    } catch {
      case _: Exception => Level.Warn
    }
  )
}
