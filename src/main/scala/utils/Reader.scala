import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types._
import com.typesafe.scalalogging.LazyLogging

/**
 * Utilitaire de lecture de données avec fonctions PURES
 * Suit les paradigmes fonctionnels demandés
 */
object Reader extends LazyLogging {

  /**
   * Fonction PURE pour lire un fichier CSV et retourner un DataFrame
   * 
   * @param path Chemin vers le fichier CSV
   * @param spark Session Spark implicite
   * @return DataFrame contenant les données du CSV
   */
  def sourceFromCsv(path: String)(implicit spark: SparkSession): DataFrame = {
    logger.info(s" Lecture du fichier CSV: $path")
    
    spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .option("multiline", "true")
      .option("escape", "\"")
      .csv(path)
  }

  /**
   * Fonction PURE pour lire depuis PostgreSQL
   * 
   * @param tableName Nom de la table PostgreSQL
   * @param spark Session Spark implicite
   * @return DataFrame contenant les données de la table
   */
  def sourceFromPostgreSQL(tableName: String)(implicit spark: SparkSession): DataFrame = {
    logger.info(s" Lecture de la table PostgreSQL: $tableName")
    
    val jdbcUrl = "jdbc:postgresql://localhost:54876/uber_eats_analysis"
    val connectionProperties = new java.util.Properties()
    connectionProperties.put("user", "postgres")
    connectionProperties.put("password", "postgres")
    connectionProperties.put("driver", "org.postgresql.Driver")
    
    spark.read
      .jdbc(jdbcUrl, tableName, connectionProperties)
  }

  /**
   * Fonction PURE pour lire un fichier Parquet
   * 
   * @param path Chemin vers le fichier Parquet
   * @param spark Session Spark implicite
   * @return DataFrame contenant les données du Parquet
   */
  def sourceFromParquet(path: String)(implicit spark: SparkSession): DataFrame = {
    logger.info(s" Lecture du fichier Parquet: $path")
    
    spark.read.parquet(path)
  }
}
