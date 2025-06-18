package utils

import org.apache.spark.sql.{DataFrame, SaveMode}
import com.typesafe.scalalogging.LazyLogging

/**
 * Utilitaire d'écriture de données avec fonctions PURES
 * Retourne des valeurs booléennes pour indiquer le succès/échec
 */
object Writer extends LazyLogging {

  /**
   * Fonction PURE pour écrire un DataFrame en CSV
   * 
   * @param dataFrame DataFrame à écrire
   * @param destination Chemin de destination
   * @return Boolean indiquant si l'écriture a réussi
   */
  def writeToCsv(dataFrame: DataFrame, destination: String): Boolean = {
    try {
      logger.info(s" Écriture CSV vers: $destination")
      
      dataFrame.coalesce(1)
        .write
        .mode(SaveMode.Overwrite)
        .option("header", "true")
        .csv(destination)
      
      logger.info(s" Écriture CSV réussie: $destination")
      true
    } catch {
      case ex: Exception =>
        logger.error(s" Échec écriture CSV vers $destination: ${ex.getMessage}")
        false
    }
  }

  /**
   * Fonction PURE pour écrire un DataFrame en Parquet
   * 
   * @param dataFrame DataFrame à écrire
   * @param destination Chemin de destination
   * @return Boolean indiquant si l'écriture a réussi
   */
  def writeToParquet(dataFrame: DataFrame, destination: String): Boolean = {
    try {
      logger.info(s" Écriture Parquet vers: $destination")
      
      dataFrame.write
        .mode(SaveMode.Overwrite)
        .parquet(destination)
      
      logger.info(s" Écriture Parquet réussie: $destination")
      true
    } catch {
      case ex: Exception =>
        logger.error(s" Échec écriture Parquet vers $destination: ${ex.getMessage}")
        false
    }
  }

  /**
   * Fonction PURE pour écrire un DataFrame en JSON
   * 
   * @param dataFrame DataFrame à écrire
   * @param destination Chemin de destination
   * @return Boolean indiquant si l'écriture a réussi
   */
  def writeToJson(dataFrame: DataFrame, destination: String): Boolean = {
    try {
      logger.info(s" Écriture JSON vers: $destination")
      
      dataFrame.coalesce(1)
        .write
        .mode(SaveMode.Overwrite)
        .json(destination)
      
      logger.info(s" Écriture JSON réussie: $destination")
      true
    } catch {
      case ex: Exception =>
        logger.error(s" Échec écriture JSON vers $destination: ${ex.getMessage}")
        false
    }
  }

  /**
   * Fonction utilitaire pour afficher des statistiques du DataFrame
   * 
   * @param dataFrame DataFrame à analyser
   * @param name Nom descriptif du DataFrame
   */
  def showStats(dataFrame: DataFrame, name: String): Unit = {
    logger.info(s" Statistiques de $name:")
    logger.info(s"   - Nombre de lignes: ${dataFrame.count()}")
    logger.info(s"   - Nombre de colonnes: ${dataFrame.columns.length}")
    logger.info(s"   - Colonnes: ${dataFrame.columns.mkString(", ")}")
  }
}
