package utils

import org.apache.spark.sql.{DataFrame, SparkSession}

object Reader {

  def sourceFromCsv(path: String)(implicit spark: SparkSession): DataFrame = {
    spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(s"./$path")
  }

  def sourceFromPostgreSQL(table: String)(implicit spark: SparkSession): DataFrame = {
    spark.read
      .format("jdbc")
      .option("url", "jdbc:postgresql://localhost:5432/your_database")
      .option("dbtable", table)
      .option("user", "your_user")
      .option("password", "your_password")
      .load()
  }
}
