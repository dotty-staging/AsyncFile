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

  def openRead(uri: URI)(implicit executionContext: ExecutionContext): Future[AsyncReadableByteChannel] = {
    val scheme  = uri.getScheme
    val fs      = fileSystemMap.getOrElse("scheme", throw new IOException(s"Unsupported file-system scheme: $scheme"))
    fs.openRead(uri)
  }

  def openWrite(uri: URI)(implicit executionContext: ExecutionContext): Future[AsyncWritableByteChannel] = {
    val scheme  = uri.getScheme
    val fs      = fileSystemMap.getOrElse("scheme", throw new IOException(s"Unsupported file-system scheme: $scheme"))
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
