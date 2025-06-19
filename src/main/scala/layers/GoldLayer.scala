package layers

import org.apache.spark.sql.SparkSession
import utils.{Reader, Writer}

object BuildDataMarts {

  def run()(implicit spark: SparkSession): Unit = {
    val bronzeBasePath = "src/data/bronze"
    val hiveDatabase = "uber_eats"
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))
}