package layers

import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.functions._

object GoldLayer {

  def run()(implicit spark: SparkSession): Unit = {
    println("💰 Starting Gold Layer transformation (separate aggregations)...")

    try {
      val menus = spark.read.format("hive").table("uber_eats.menus")
      val restaurants = spark.read.format("hive").table("uber_eats.restaurants")

      // ✅ Résumé des menus (note: score supprimé)
      val menuAgg = menus.groupBy("category")
        .agg(
          avg("price").alias("average_price"),
          count("*").alias("menu_items_count")
        )

      menuAgg.write.mode("overwrite").parquet("src/data/gold/menu_summary")
      println("✅ Saved: src/data/gold/menu_summary")

      // ✅ Résumé des restaurants (on garde le score ici)
      val restaurantAgg = restaurants.groupBy("category")
        .agg(
          count("*").alias("restaurant_count"),
          avg("score").alias("average_score")
        )

      restaurantAgg.write.mode("overwrite").parquet("src/data/gold/restaurant_summary")
      println("✅ Saved: src/data/gold/restaurant_summary")

    } catch {
      case e: Exception =>
        println(s"❌ Gold Layer failed: ${e.getMessage}")
        e.printStackTrace()
    }
  }
}
