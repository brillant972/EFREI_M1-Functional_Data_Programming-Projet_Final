ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "uber-eats-restaurant-analysis",
    organization := "com.efrei.functional",
    
    // Dépendances principales
    libraryDependencies ++= Seq(
      // Apache Spark
      "org.apache.spark" %% "spark-core" % "3.5.0" % "provided",
      "org.apache.spark" %% "spark-sql" % "3.5.0" % "provided",
      
      // PostgreSQL Driver
      "org.postgresql" % "postgresql" % "42.7.1",
      
      // Configuration
      "com.typesafe" % "config" % "1.4.3",
      
      // Logging
      "ch.qos.logback" % "logback-classic" % "1.4.14",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      
      // Tests
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "org.scalatestplus" %% "mockito-4-11" % "3.2.17.0" % Test
    ),
    
    // Options du compilateur Scala
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard"
    ),
    
    // Paramètres JVM pour l'exécution
    fork := true,
    javaOptions ++= Seq(
      "-Xmx2g",
      "-XX:+UseG1GC"
    )
  )
