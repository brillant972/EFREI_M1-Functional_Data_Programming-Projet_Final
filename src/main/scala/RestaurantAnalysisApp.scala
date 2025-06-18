import org.apache.spark.sql.SparkSession
import com.typesafe.scalalogging.LazyLogging
import layers.BronzeLayer

/**
 * Application principale d'analyse des restaurants Uber Eats
 * Architecture médaillon : Bronze → Silver → Gold
 */
object RestaurantAnalysisApp extends LazyLogging {

  def main(args: Array[String]): Unit = {
    logger.info("🍽️ Démarrage de l'analyse des restaurants Uber Eats")    // Création de la session Spark
    implicit val spark: SparkSession = SparkSession.builder()
      .appName("Uber Eats Restaurant Analysis")
      .master("local[*]")
      .config("spark.sql.adaptive.enabled", "true")
      .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
      .getOrCreate()

    // Configuration du niveau de log
    spark.sparkContext.setLogLevel("WARN")

    try {
      logger.info("🏗️ Architecture médaillon - Pipeline de données")

      // === COUCHE BRONZE ===
      logger.info("🥉 Exécution de la couche Bronze")
      val bronzeSuccess = BronzeLayer.run()

      if (bronzeSuccess) {
        logger.info("✅ Couche Bronze terminée avec succès")
        
        // TODO: Implémenter les couches Silver et Gold
        logger.info("🥈 Couche Silver - À implémenter")
        logger.info("🥇 Couche Gold - À implémenter")
        
        logger.info("🎉 Pipeline d'analyse terminé avec succès")
      } else {
        logger.error("❌ Échec de la couche Bronze - Arrêt du pipeline")
      }

    } catch {
      case e: Exception =>
        logger.error(s"💥 Erreur dans l'application : ${e.getMessage}")
        e.printStackTrace()
    } finally {
      // Arrêt de la session Spark
      spark.stop()
      logger.info("🔚 Application terminée")
    }
  }
}
