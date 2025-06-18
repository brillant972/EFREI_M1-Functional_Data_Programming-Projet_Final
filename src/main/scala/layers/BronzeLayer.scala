package layers

import org.apache.spark.sql.SparkSession
import com.typesafe.scalalogging.LazyLogging
import utils.{Reader, Writer}

/**
 * Couche Bronze - Ingestion des données brutes
 */
object BronzeLayer extends LazyLogging {

  /**
   * Exécution de la couche Bronze
   */
  def run()(implicit spark: SparkSession): Boolean = {
    logger.info("Démarrage couche Bronze")

    val sources = List(
      ("restaurants.csv", "src/data/bronze/restaurants", "CSV"),
      ("restaurant-menus.csv", "src/data/bronze/menus", "CSV")
      // Ajouter PostgreSQL plus tard
      // ("restaurant_ratings_history", "src/data/bronze/ratings", "POSTGRES"),
      // ("delivery_performance", "src/data/bronze/delivery", "POSTGRES")
    )

    val results = sources.map { case (source, output, sourceType) =>
      processSource(source, output, sourceType)
    }

    val success = results.forall(identity)
    
    if (success) {
      logger.info("[OK] Couche Bronze terminée")
    } else {
      logger.error("[NOK] Échec couche Bronze")
    }

    success
  }

  /**
   * Traitement générique d'une source de données
   */
  private def processSource(source: String, output: String, sourceType: String)(implicit spark: SparkSession): Boolean = {
    logger.info(s"Ingestion: $source")

    try {
      // Lecture selon le type de source
      val df = sourceType match {
        case "CSV" => Reader.sourceFromCsv(source)
        case "POSTGRES" => Reader.sourceFromPostgreSQL(source)
        case _ => throw new IllegalArgumentException(s"Type de source non supporté: $sourceType")
      }

      df.cache()
      
      // Stats simples
      val count = df.count()
      logger.info(s"📊 $source: $count lignes")      // Écriture Parquet
      val success = Writer.writeToParquet(df, output)
      
      if (success) {
        logger.info(s"[OK] $source → $output")
      } else {
        logger.error(s"[NOK] Échec écriture $source")
      }

      df.unpersist()
      success

    } catch {
      case e: Exception =>
        logger.error(s"[NOK] Erreur $source: ${e.getMessage}")
        false
    }
  }
}
