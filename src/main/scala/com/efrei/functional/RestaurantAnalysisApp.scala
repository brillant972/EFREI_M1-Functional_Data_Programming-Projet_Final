package com.efrei.functional

import org.apache.spark.sql.SparkSession
import layers.BronzeLayer

object RestaurantAnalysisApp {

  def main(args: Array[String]): Unit = {
    // Logging configuration
    System.setProperty("log4j.configuration", "log4j.properties")

    // SparkSession
    implicit val spark: SparkSession = SparkSession.builder()
      .appName("Uber Eats Restaurant Analysis")
      .master("local[*]")
      .config("spark.driver.memory", "2g")
      .config("spark.executor.memory", "2g")
      .config("spark.sql.adaptive.enabled", "true")
      .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
      .config("spark.driver.extraJavaOptions", "-Dlog4j.configuration=log4j.properties")
      .config("spark.executor.extraJavaOptions", "-Dlog4j.configuration=log4j.properties")
      .enableHiveSupport()
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    try {
      // 🥉 Run Bronze layer
      val bronzeSuccess = BronzeLayer.run()

      if (bronzeSuccess) {
        // 🥈 Continue to Hive load step
        CleanAllToHive.run()
      } else {
        println("❌ Bronze layer failed. Skipping Hive load.")
      }

    } catch {
      case e: Exception =>
        println(s"💥 Pipeline failed: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
    }
  }
}
