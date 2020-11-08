/*
 *  AsyncFileSystem.scala
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

import scala.concurrent.{ExecutionContext, Future}

trait AsyncFileSystem {
  def scheme: String
  def name  : String

  def openRead (uri: URI)(implicit executionContext: ExecutionContext): Future[AsyncReadableByteChannel]
  def openWrite(uri: URI, append: Boolean = false)(implicit executionContext: ExecutionContext): Future[AsyncWritableByteChannel]

  def mkDir (uri: URI): Future[Boolean]
  def mkDirs(uri: URI): Future[Boolean]
  def delete(uri: URI): Future[Boolean]
}