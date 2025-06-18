package utils

import org.apache.spark.sql.DataFrame

object Writer {
  def writeToParquet(df: DataFrame, outputPath: String): Boolean = {
    try {
      df.write
        .mode("overwrite")
        .parquet(outputPath)
      true
    } catch {
      case _: Exception => false
    }
  }
}
