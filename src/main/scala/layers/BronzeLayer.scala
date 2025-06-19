package layers

import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.{Reader, Writer, ConfigManager}
import com.typesafe.scalalogging.LazyLogging

/**
 * Couche Bronze - Ingestion des données brutes
 * Lecture depuis CSV et PostgreSQL, écriture en Parquet
 */
object BronzeLayer extends LazyLogging {
  /**
   * Extraction des restaurants depuis CSV
   */
  def extractRestaurantsFromCSV(spark: SparkSession): DataFrame = {
    implicit val sparkSession: SparkSession = spark
    logger.info("🍽️ Extraction des restaurants depuis CSV")
    val df = Reader.sourceFromCsv(ConfigManager.restaurantsCsvPath)
    logger.info(s"✅ ${df.count()} restaurants extraits")
    df
  }

  /**
   * Extraction des menus depuis CSV
   */
  def extractMenusFromCSV(spark: SparkSession): DataFrame = {
    implicit val sparkSession: SparkSession = spark
    logger.info("📋 Extraction des menus depuis CSV")
    val df = Reader.sourceFromCsv(ConfigManager.menusCsvPath)
    logger.info(s"✅ ${df.count()} items de menu extraits")
    df
  }

  /**
   * Extraction des données temporelles depuis PostgreSQL
   */
  def extractRatingsFromPostgreSQL(spark: SparkSession): DataFrame = {
    implicit val sparkSession: SparkSession = spark
    logger.info("📊 Extraction historique ratings depuis PostgreSQL")
    val df = Reader.sourceFromPostgreSQL(ConfigManager.ratingsHistoryTable)
    logger.info(s"✅ ${df.count()} enregistrements ratings extraits")
    df
  }

  def extractDeliveryFromPostgreSQL(spark: SparkSession): DataFrame = {
    implicit val sparkSession: SparkSession = spark
    logger.info("🚚 Extraction performance delivery depuis PostgreSQL")
    val df = Reader.sourceFromPostgreSQL(ConfigManager.deliveryPerformanceTable)
    logger.info(s"✅ ${df.count()} enregistrements delivery extraits")
    df
  }

  def extractCampaignsFromPostgreSQL(spark: SparkSession): DataFrame = {
    implicit val sparkSession: SparkSession = spark
    logger.info("📢 Extraction campagnes promotionnelles depuis PostgreSQL")
    val df = Reader.sourceFromPostgreSQL(ConfigManager.promotionalCampaignsTable)
    logger.info(s"✅ ${df.count()} campagnes extraites")
    df
  }

  /**
   * Écriture en format Parquet dans la couche Bronze
   */
  def writeToParquet(df: DataFrame, tableName: String): Unit = {
    val outputPath = s"${ConfigManager.bronzePath}/$tableName/"
    logger.info(s"💾 Écriture $tableName vers $outputPath")
    
    val success = Writer.writeToParquet(df, outputPath)
    if (success) {
      logger.info(s"✅ $tableName écrit avec succès en Bronze")
    } else {
      logger.error(s"❌ Échec écriture $tableName en Bronze")
    }
  }

  /**
   * Pipeline complet de la couche Bronze
   */
  def run(spark: SparkSession): Unit = {
    logger.info("🥉 === DÉBUT COUCHE BRONZE ===")
    
    try {
      // ==============================
      //  EXTRACT depuis CSV
      // ==============================
      val restaurantsDF = extractRestaurantsFromCSV(spark)
      val menusDF = extractMenusFromCSV(spark)
      
      // ==============================
      //  EXTRACT depuis PostgreSQL
      // ==============================
      val ratingsDF = extractRatingsFromPostgreSQL(spark)
      val deliveryDF = extractDeliveryFromPostgreSQL(spark)
      val campaignsDF = extractCampaignsFromPostgreSQL(spark)
      
      // ==============================
      //  LOAD vers Bronze (Parquet)
      // ==============================
      writeToParquet(restaurantsDF, "restaurants")
      writeToParquet(menusDF, "menu_items")
      writeToParquet(ratingsDF, "ratings_history")
      writeToParquet(deliveryDF, "delivery_performance")
      writeToParquet(campaignsDF, "promotional_campaigns")
      
      logger.info("🥉 === COUCHE BRONZE TERMINÉE AVEC SUCCÈS ===")
      
    } catch {
      case e: Exception =>
        logger.error(s"❌ Erreur dans la couche Bronze: ${e.getMessage}")
        throw e
    }
  }
}