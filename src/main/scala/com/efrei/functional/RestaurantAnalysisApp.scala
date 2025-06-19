package com.efrei.functional

import org.apache.spark.sql.{SparkSession, DataFrame}
import layers.{BronzeLayer, CleanAllToHive, GoldLayer}

object RestaurantAnalysisApp {

  def main(args: Array[String]): Unit = {
    System.setProperty("log4j.configuration", "log4j.properties")

    implicit val spark: SparkSession = SparkSession.builder()
      .appName("Uber Eats Restaurant Analysis")
      .master("local[*]")
      .config("spark.driver.memory", "2g")
      .config("spark.executor.memory", "2g")
      .config("spark.sql.adaptive.enabled", "true")
      .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
      .config("spark.sql.warehouse.dir", "C:/hive-warehouse")
      .config("spark.driver.extraJavaOptions", "-Dlog4j.configuration=log4j.properties")
      .config("spark.executor.extraJavaOptions", "-Dlog4j.configuration=log4j.properties")
      .enableHiveSupport()
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    try {
      // 🥉 Bronze layer
      val bronzeSuccess = BronzeLayer.run()

      if (bronzeSuccess) {
        // 🥈 Silver: Load Parquet into Hive
        CleanAllToHive.run()

        // Load all Hive tables from the 'uber_eats' database
        val hiveDatabase = "uber_eats"
        spark.sql(s"USE $hiveDatabase")

        val tables: Seq[String] = spark.catalog.listTables(hiveDatabase)
          .filter(_.tableType == "MANAGED")
          .collect()
          .map(_.name)

        if (tables.contains("menus") && tables.contains("restaurants")) {
          // 🥇 Gold Layer: transform and aggregate
          GoldLayer.run()
        } else {
          println("❌ Required tables 'menus' and/or 'restaurants' not found in Hive. Skipping Gold layer.")
        }

      } else {
        println("Bronze layer failed. Skipping Silver and Gold.")
      }

    } catch {
      case e: Exception =>
        println(s"Pipeline failed: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
    }
  }
}
