package fs2.kafka.docs

import fs2.kafka.build.info._

import java.nio.file.{FileSystems, Path}

object Main {
  def sourceDirectoryPath(rest: String*): Path =
    FileSystems.getDefault.getPath(sourceDirectory.getAbsolutePath, rest: _*)

  def minorVersion(version: String): String = {
    val Array(major, minor, _) = version.split('.')
    s"$major.$minor"
  }

  def main(args: Array[String]): Unit = {
    val settings = mdoc
      .MainSettings()
      .withSiteVariables {
        Map(
          "ORGANIZATION" -> organization,
          "MODULE_NAME" -> moduleName,
          "LATEST_VERSION" -> latestVersion,
          "LATEST_MINOR_VERSION" -> minorVersion(latestVersion),
          "DOCS_SCALA_MINOR_VERSION" -> minorVersion(scalaVersion),
          "FS2_VERSION" -> fs2Version,
          "KAFKA_VERSION" -> kafkaVersion,
          "KAFKA_DOCS_VERSION" -> minorVersion(kafkaVersion).filter(_ != '.'),
          "SCALA_PUBLISH_VERSIONS" -> {
            val minorVersions = crossScalaVersions.map(minorVersion)
            if (minorVersions.size <= 2) minorVersions.mkString(" and ")
            else minorVersions.init.mkString(", ") ++ " and " ++ minorVersions.last
          }
        )
      }
      .withScalacOptions(scalacOptions.mkString(" "))
      .withIn(sourceDirectoryPath("main", "mdoc"))
      .withArgs(args.toList)

    val exitCode = mdoc.Main.process(settings)
    if (exitCode != 0) sys.exit(exitCode)
  }
}
