import org.apache.spark.sql.SparkSession
import layers.BronzeLayer
import utils.ConfigManager
import com.typesafe.scalalogging.LazyLogging

/**
 * Application principale d'analyse des restaurants Uber Eats
 * Architecture médaillon : Bronze → Silver → Gold
 */
object RestaurantAnalysisApp extends LazyLogging {

  def main(args: Array[String]): Unit = {
    logger.info("🚀 === DÉMARRAGE ANALYSE RESTAURANTS UBER EATS ===")

    // Configuration Spark
    val spark = SparkSession.builder()
      .appName(ConfigManager.sparkAppName)
      .master("local[*]")
      .config("spark.driver.host", "127.0.0.1")
      .config("spark.sql.adaptive.enabled", "true")
      .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
      .getOrCreate()

    try {
      logger.info("✅ Session Spark initialisée")
      
      // ==============================
      //  COUCHE BRONZE - Ingestion données brutes
      // ==============================
      BronzeLayer.run(spark)
      
      // ==============================
      //  COUCHE SILVER - Transformations et nettoyage
      // ==============================
      // TODO: SilverLayer.run(spark)
      logger.info("⏳ Couche Silver - À implémenter")
      
      // ==============================
      //  COUCHE GOLD - Analytics et agrégations
      // ==============================
      // TODO: GoldLayer.run(spark)
      logger.info("⏳ Couche Gold - À implémenter")
      
      logger.info("🎉 === ANALYSE TERMINÉE AVEC SUCCÈS ===")
      
    } catch {
      case e: Exception =>
        logger.error(s"💥 Erreur dans l'application: ${e.getMessage}")
        e.printStackTrace()
        sys.exit(1)
    } finally {
      spark.stop()
      logger.info("🛑 Session Spark fermée")
    }
  }
}