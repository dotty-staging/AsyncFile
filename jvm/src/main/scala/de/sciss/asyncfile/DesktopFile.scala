/*
 *  DesktopFile.scala
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

import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.StandardOpenOption

import de.sciss.asyncfile.impl.DesktopFileImpl

import scala.concurrent.ExecutionContext

/** A wrapper around `java.nio.channels.AsynchronousFileChannel` implementing the
 * `AsyncWritableByteChannel` interface.
 */
object DesktopFile {
  def openRead(path: String)(implicit executionContext: ExecutionContext): DesktopFile = {
    val p   = new File(path).toPath
    val jch = AsynchronousFileChannel.open(p,
      StandardOpenOption.READ,
    )
    new DesktopFileImpl(jch, path, readOnly = true)
  }

  def openWrite(path: String, append: Boolean = false)
               (implicit executionContext: ExecutionContext): DesktopWritableFile = {
    val f   = new File(path)
    val p   = f.toPath
    val jch = if (append) {
      AsynchronousFileChannel.open(p,
        StandardOpenOption.WRITE,
        StandardOpenOption.CREATE,
      )
    } else {
      // there is a very weird thing: if the file already
      // exists, although `TRUNCATE_EXISTING` is set and works,
      // flushing and closing the file takes _a lot_ (>5 times) longer
      // than preemptively deleting the file before re-open.
      f.delete()
      AsynchronousFileChannel.open(p,
        StandardOpenOption.WRITE,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING,
      )
    }
    new DesktopFileImpl(jch, path, readOnly = false)
  }
}
trait DesktopFile         extends AsyncReadableByteChannel
trait DesktopWritableFile extends DesktopFile with AsyncWritableByteChannel

