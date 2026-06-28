ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.7"


coverageExcludedPackages :=
  "de\\.htwg\\.se\\.machikoro\\.remake\\.controller\\.mementoPatern\\..*;" +
    "de\\.htwg\\.se\\.machikoro\\.remake\\.view\\..*"


coverageExcludedFiles := Seq(
  ".*[/\\\\]AppModule\\.scala",
  ".*[/\\\\]main\\.scala"
).mkString(";")

Compile / run / mainClass := Some("de.htwg.se.machikoro.remake.main")

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.20",
  "org.scalatest" %% "scalatest" % "3.2.20" % "test",
  "org.scalafx" %% "scalafx" % "21.0.0-R32",
  "io.circe" %% "circe-core" % "0.14.10",
  "io.circe" %% "circe-generic" % "0.14.10",
  "io.circe" %% "circe-parser" % "0.14.10",
  "net.codingwell" %% "scala-guice" % "7.0.0",
  "org.scala-lang.modules" %% "scala-xml" % "2.4.0"
)

lazy val root = (project in file("."))
  .settings(
    name := "SoftwareEngineering"
  )