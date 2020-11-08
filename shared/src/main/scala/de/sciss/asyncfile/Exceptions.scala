package de.sciss.asyncfile

import java.io.IOException
import java.net.URI

final class FileNotFoundException         (uri: URI) extends IOException(s"Not found: $uri")
final class UnsupportedFileSystemException(uri: URI) extends IOException(s"Unsupported file system: $uri")
