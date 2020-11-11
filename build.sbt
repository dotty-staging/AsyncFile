lazy val baseName  = "AsyncFile"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "0.1.0"
lazy val mimaVersion    = "0.1.0"

lazy val deps = new {
  val main = new {
    val dom       = "1.1.0"
    val log       = "0.1.1"
  }
  val test = new {
    val scalaTest = "3.2.2"
  }
}

lazy val commonJvmSettings = Seq(
  crossScalaVersions := Seq("3.0.0-M1", "2.13.3", "2.12.12"),
)

lazy val root = crossProject(JSPlatform, JVMPlatform).in(file("."))
  .enablePlugins(BuildInfoPlugin)
  .jvmSettings(commonJvmSettings)
  .settings(
    name               := baseName,
    version            := projectVersion,
    organization       := "de.sciss",
    scalaVersion       := "2.13.3",
    description        := "A library to read and write files asynchronously on the JVM and JS",
    homepage           := Some(url(s"https://github.com/Sciss/${name.value}")),
    licenses           := Seq("AGPL v3+" -> url("http://www.gnu.org/licenses/agpl-3.0.txt")),
    mimaPreviousArtifacts := Set("de.sciss" %% baseNameL % mimaVersion),
    initialCommands in console := """import de.sciss.synth.io._""",
    libraryDependencies ++= Seq(
      "de.sciss"      %%% "log"       % deps.main.log,
//      "org.scalatest" %%% "scalatest" % deps.test.scalaTest % Test,
    ),
    scalacOptions ++= Seq(
      "-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xlint", "-Xsource:2.13",
    ),
    scalacOptions in (Compile, compile) ++= {
      val jdkGt8  = scala.util.Properties.isJavaAtLeast("9")
      val sv      = scalaVersion.value
      val isDotty = sv.startsWith("3.") // https://github.com/lampepfl/dotty/issues/8634
      val sq0     = (if (!isDotty && jdkGt8) List("-release", "8") else Nil)
      if (sv.startsWith("2.12.")) sq0 else "-Wvalue-discard" :: sq0
    }, // JDK >8 breaks API; skip scala-doc
    // ---- build info ----
    buildInfoKeys := Seq(name, organization, version, scalaVersion, description,
      BuildInfoKey.map(homepage) { case (k, opt)           => k -> opt.get },
      BuildInfoKey.map(licenses) { case (_, Seq((lic, _))) => "license" -> lic }
    ),
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % deps.main.dom,
    ),
  )
  .settings(publishSettings)


// ---- publishing ----
lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    Some(if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := { val n = name.value
<scm>
  <url>git@git.iem.at:sciss/{n}.git</url>
  <connection>scm:git:git@git.iem.at:sciss/{n}.git</connection>
</scm>
<developers>
  <developer>
    <id>sciss</id>
    <name>Hanns Holger Rutz</name>
    <url>http://www.sciss.de</url>
  </developer>
</developers>
  }
)
