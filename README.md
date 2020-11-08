# AsyncFile

[![Build Status](https://travis-ci.org/Sciss/AsyncFile.svg?branch=main)](https://travis-ci.org/Sciss/AsyncFile)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/asyncfile_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/asyncfile_2.13)

## statement

AsyncFile is a Scala library to read and write files asynchronously with a common API both for the JVM and for
JavaScript (Scala.js). It is (C)opyright 2020 by Hanns Holger Rutz. All rights reserved.
This project is released under
the [GNU Affero General Public License](https://git.iem.at/sciss/AsyncFile/raw/main/LICENSE) v3+ and comes
with absolutely no warranties. To contact the author, send an e-mail to `contact at sciss.de`.

__This project is still in experimental state!__

## requirements / installation

This project builds with sbt against Scala 2.13, 2.12, Dotty (JVM) and Scala 2.13 (JS).

To use the library in your project:

    "de.sciss" %% "asyncfile" % v

The current version `v` is `"0.1.0"`

## contributing

Please see the file [CONTRIBUTING.md](CONTRIBUTING.md)

## getting started

A good way to understand the library is to look at [AudioFile](https://github.com/Sciss/AudioFile/) 
which is another library that uses AsyncFile.

The idea is to use `java.net.URI` as the common path representation, and to support the `file` scheme on the JVM,
mapping to `java.nio.channels.AsynchronousFileChannel`, while introducing a new scheme `idb` for JS which is
a virtual file system backed by IndexedDB, thus supporting client-side Scala.js in the browser. New file systems
can be registered using `AsyncFile.addFileSystem`.

On the desktop, you can use `DesktopFileSystem.openRead(uri)` and `DesktopFileSystem.openWrite(uri)`, or directly
call `DesktopFile.openRead(path)` and `DesktopFile.openWrite(path)`. 
In the browser, you can use `IndexedDBFileSystem.openRead(uri)` and `IndexedDBFileSystem.openWrite(uri)`, or directly
call `IndexedDBFile.openRead(path)` and `DesktopFile.openWrite(path)`.

In a platform neutral way, you can write `AsyncFile.openRead(uri)` and `AsyncFile.openWrite(uri)`.
