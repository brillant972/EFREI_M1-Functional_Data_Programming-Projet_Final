package com.efrei.functional

import org.apache.spark.sql.{SparkSession, DataFrame}
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CleanAllToHive {

  def run()(implicit spark: SparkSession): Unit = {
    val bronzeBasePath = "src/data/bronze"
    val hiveDatabase = "uber_eats"
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))

    // Create Hive DB if needed
    spark.sql(s"CREATE DATABASE IF NOT EXISTS $hiveDatabase")
    spark.sql(s"USE $hiveDatabase")

    val folders = new File(bronzeBasePath)
      .listFiles()
      .filter(_.isDirectory)
      .map(_.getName)

    folders.foreach { folderName =>
      val inputPath = s"$bronzeBasePath/$folderName"
      val tableNameWithDate = s"${folderName}_$today"

      try {
        val df = spark.read
          .parquet(inputPath)
          .na.drop()

        val cleanedDf = if (folderName == "delivery_performance") df.drop("last_updated") else df

        // Repartition to 1 to merge parquet part files
        cleanedDf
          .repartition(1)
          .write
          .mode("overwrite")
          .format("hive")
          .saveAsTable(s"$hiveDatabase.$tableNameWithDate")

        println(s"✅ Table '$tableNameWithDate' cleaned, merged, and saved to Hive")

      } catch {
        case e: Exception =>
          println(s"❌ Failed to process '$folderName': ${e.getMessage}")
      }
    }
  }
}
