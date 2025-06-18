ThisBuild / scalaVersion := "2.13.12"
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "uber-eats-restaurant-analysis",
    organization := "com.efrei.functional",

    Compile / mainClass := Some("com.efrei.functional.RestaurantAnalysisApp"),

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.5.0",
      "org.apache.spark" %% "spark-sql" % "3.5.0",
      "org.apache.spark" %% "spark-hive" % "3.5.0",
      "org.postgresql" % "postgresql" % "42.7.1",
      "org.apache.logging.log4j" % "log4j-core" % "2.20.0",
      "org.apache.logging.log4j" % "log4j-api" % "2.20.0",
      "org.apache.logging.log4j" % "log4j-slf4j2-impl" % "2.20.0",
      "com.typesafe" % "config" % "1.4.3",
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "org.scalatestplus" %% "mockito-4-11" % "3.2.17.0" % Test
    ),

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard"
    ),

    fork := true,
    Compile / run / fork := true,

    javaOptions ++= Seq(
      "-Xmx4g",
      "-XX:+UseG1GC"
    ),

    Compile / run / javaOptions ++= Seq(
      s"-Dlog4j.configurationFile=${baseDirectory.value}/src/main/resources/log4j2.properties"
    )
  )
