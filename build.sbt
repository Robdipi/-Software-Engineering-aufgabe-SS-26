ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"
coverageExcludedFiles := "de.htwg.se.machikoro.remake.main"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.20"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % "test"
libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32"

lazy val root = (project in file("."))
  .settings(
    name := "SoftwareEngineering"
  )
