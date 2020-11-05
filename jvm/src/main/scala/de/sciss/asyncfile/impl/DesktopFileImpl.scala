/*
 *  DesktopFileImpl.scala
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
package impl

import java.io.{File, IOException}
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler, ReadPendingException, WritePendingException}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}

import scala.concurrent.{ExecutionContext, Future, Promise}

/** A wrapper around `java.nio.channels.AsynchronousFileChannel` implementing the
  * `AsyncWritableByteChannel` interface.
  */
final class DesktopFileImpl(peer: AsynchronousFileChannel, f: File, readOnly: Boolean)
                           (implicit val executionContext: ExecutionContext)
    extends DesktopWritableFile with CompletionHandler[java.lang.Integer, Promise[Int]] {

//    private[this] val reqThread   = Thread.currentThread()

  private[this] val posRef      = new AtomicLong(0L)
  private[this] val pendingRef  = new AtomicBoolean(false)

  def position        : Long        = posRef.get()
  def position_=(value: Long): Unit = posRef.set(value)

  def skip(len: Long): Unit = {
    posRef.addAndGet(len)
    ()
  }

  def read(dst: ByteBuffer): Future[Int] = {
//      require (Thread.currentThread() == reqThread)

//      println(" ==> ")
    if (!pendingRef.compareAndSet(false, true)) throw new ReadPendingException()  // XXX TODO should distinguish read/write

    val pos = posRef.get()
    val pr  = Promise[Int]()
//      println(s"peer.read($dst, $pos, ...)")
    peer.read(dst, pos, pr, this)
    pr.future
  }

  def write(src: ByteBuffer): Future[Int] = {
    if (readOnly) throw new IOException(s"File $f was opened for reading only")

    if (!pendingRef.compareAndSet(false, true)) throw new WritePendingException() // XXX TODO should distinguish read/write

    val pos = posRef.get()
    val pr  = Promise[Int]()
    peer.write(src, pos, pr, this)
    pr.future
  }

  def size: Long = peer.size()

  def remaining: Long = size - position

  def close(): Unit = {
    if (pendingRef.get()) throw new ReadPendingException()  // XXX TODO should distinguish read/write
    peer.close()
  }

  def isOpen: Boolean = peer.isOpen

  // ---- CompletionHandler ----

  def completed (res: Integer, pr: Promise[Int]): Unit = {
//      println(s"completed ${Thread.currentThread().hashCode().toHexString}")
    posRef.addAndGet(res.toLong)
//      println(" <== ")
    if (pendingRef.compareAndSet(true, false)) {
      pr.success(res)
    } else {
      pr.failure(new AssertionError("No pending read"))
    }
  }

  def failed(e: Throwable, pr: Promise[Int]): Unit = {
//      println(s"failed ${Thread.currentThread().hashCode().toHexString}")
//      println(" <== ")
    if (pendingRef.compareAndSet(true, false)) {
      pr.failure(e)
    } else {
      pr.failure(new AssertionError("No pending read"))
    }
  }
}