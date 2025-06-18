package layers

import org.apache.spark.sql.SparkSession
import utils.{Reader, Writer}

/**
 * Couche Bronze - Ingestion des données brutes
 */
object BronzeLayer {

  /**
   * Exécution de la couche Bronze
   */
  def run()(implicit spark: SparkSession): Boolean = {
    println("🥉 Démarrage couche Bronze")

    val sources = List(
      ("restaurants.csv", "src/data/bronze/restaurants", "CSV"),
      ("restaurant-menus.csv", "src/data/bronze/menus", "CSV")
    )

    val results = sources.map { case (source, output, sourceType) =>
      processSource(source, output, sourceType)
    }

    val success = results.forall(identity)

    if (success) println("✅ Couche Bronze terminée")
    else println("❌ Échec couche Bronze")

    success
  }

  /**
   * Traitement générique d'une source de données
   */
  private def processSource(source: String, output: String, sourceType: String)(implicit spark: SparkSession): Boolean = {
    println(s"📖 Ingestion: $source")

    try {
      val df = sourceType match {
        case "CSV" => Reader.sourceFromCsv(source)
        case "POSTGRES" => Reader.sourceFromPostgreSQL(source)
        case _ => throw new IllegalArgumentException(s"Type de source non supporté: $sourceType")
      }

      df.cache()
      println(s"📊 $source: ${df.count()} lignes")

      val success = Writer.writeToParquet(df, output)

      if (success) println(s"✅ $source → $output")
      else println(s"❌ Échec écriture $source")

      df.unpersist()
      success

    } catch {
      case e: Exception =>
        println(s"❌ Erreur $source: ${e.getMessage}")
        false
    }
  }
}
