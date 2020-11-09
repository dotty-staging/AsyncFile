package de.sciss.asyncfile

import scala.concurrent.{ExecutionContext, Future}

trait AsyncFileSystemProvider {
  /** The URI scheme of this file system, such as `"file"` (desktop) or `"idb"` (IndexedDB in the browser). */
  def scheme: String

  /** The logical name of the file system. */
  def name  : String

  def obtain()(implicit executionContext: ExecutionContext): Future[AsyncFileSystem]
}
