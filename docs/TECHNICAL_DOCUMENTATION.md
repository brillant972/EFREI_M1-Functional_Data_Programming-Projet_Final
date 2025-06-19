# Documentation Technique - Classes Utilitaires

## Vue d'ensemble

Ce document détaille les classes utilitaires du package `utils`, respectant les paradigmes de programmation fonctionnelle et les bonnes pratiques Scala.

---

## Reader.scala

### Responsabilité
Classe utilitaire contenant des **fonctions PURES** pour la lecture de données depuis différentes sources.

### Fonctions Disponibles

#### `sourceFromCsv(path: String): DataFrame`
```scala
/**
 * Lit un fichier CSV et retourne un DataFrame Spark
 * FONCTION PURE : pas d'effets de bord
 */
def sourceFromCsv(path: String): DataFrame
```

**Paramètres :**
- `path` : Chemin vers le fichier CSV

**Retour :**
- `DataFrame` : Données chargées avec en-têtes et inférence de schéma

**Exemple d'usage :**
```scala
val restaurantsDF = Reader.sourceFromCsv("restaurants.csv")
restaurantsDF.show(5)
```

#### `sourceFromPostgreSQL(tableName: String): DataFrame`
```scala
/**
 * Lit une table PostgreSQL et retourne un DataFrame
 * Utilise la configuration de ConfigManager
 */
def sourceFromPostgreSQL(tableName: String): DataFrame
```

**Paramètres :**
- `tableName` : Nom de la table PostgreSQL

**Configuration requise :**
```hocon
database {
  url = "jdbc:postgresql://localhost:54876/uber_eats_analysis"
  user = "postgres"
  password = "postgres"
}
```

#### `sourceFromParquet(path: String): DataFrame`
```scala
/**
 * Lit un fichier Parquet optimisé
 * Format recommandé pour les couches Silver/Gold
 */
def sourceFromParquet(path: String): DataFrame
```

---

## Writer.scala

### Responsabilité
Classe utilitaire contenant des **fonctions PURES** pour l'écriture de données vers différents formats.

### Fonctions Disponibles

#### `writeToCsv(df: DataFrame, destination: String): Boolean`
```scala
/**
 * Écrit un DataFrame au format CSV
 * FONCTION PURE : retourne un Boolean de statut de succès
 */
def writeToCsv(df: DataFrame, destination: String): Boolean
```

**Paramètres :**
- `df` : DataFrame à écrire
- `destination` : Chemin de destination

**Retour :**
- `Boolean` : `true` si succès, `false` si erreur

#### `writeToParquet(df: DataFrame, destination: String): Boolean`
```scala
/**
 * Écrit un DataFrame au format Parquet avec compression Snappy
 * Format recommandé pour les performances et le stockage
 */
def writeToParquet(df: DataFrame, destination: String): Boolean
```

**Avantages du Parquet :**
- **Compression** : ~70% de réduction de taille vs CSV
- **Performance** : Lectures ~10x plus rapides
- **Schéma** : Préservation des types de données

#### `writeToJson(df: DataFrame, destination: String): Boolean`
```scala
/**
 * Écrit un DataFrame au format JSON
 * Utile pour les exports vers les visualisations
 */
def writeToJson(df: DataFrame, destination: String): Boolean
```

#### `showStats(df: DataFrame, name: String): Unit`
```scala
/**
 * Affiche les statistiques d'un DataFrame
 * Utile pour le monitoring et le débogage
 */
def showStats(df: DataFrame, name: String): Unit
```

**Informations affichées :**
- Nombre de lignes
- Nombre de colonnes  
- Schéma des données
- Aperçu des premières lignes

---

## ConfigManager.scala

### Responsabilité
Gestionnaire de configuration centralisé, lit depuis le fichier `application.conf`.

### Propriétés Disponibles

#### **Sources de Données**
```scala
lazy val restaurantsCsvPath: String      // "restaurants.csv"
lazy val menusCsvPath: String            // "restaurant-menus.csv"
```

#### **Destinations Médaillon**
```scala
lazy val bronzePath: String              // "src/data/bronze"
lazy val silverPath: String              // "src/data/silver"  
lazy val goldPath: String                // "src/data/gold"
```

#### **Configuration PostgreSQL**
```scala
lazy val postgresUrl: String             // URL JDBC complète
lazy val postgresUser: String            // Utilisateur de base de données
lazy val postgresPassword: String        // Mot de passe de base de données
lazy val postgresDriver: String          // Driver JDBC
```

#### **Tables PostgreSQL**
```scala
lazy val ratingsHistoryTable: String         // "restaurant_ratings_history"
lazy val deliveryPerformanceTable: String    // "delivery_performance"
lazy val promotionalCampaignsTable: String   // "promotional_campaigns"
```

### Méthodes Utilitaires

#### `getJdbcProperties: java.util.Properties`
```scala
/**
 * Retourne les propriétés JDBC configurées
 * Prêtes pour utilisation avec Spark JDBC
 */
def getJdbcProperties: java.util.Properties
```

**Usage typique :**
```scala
val jdbcUrl = ConfigManager.postgresUrl
val props = ConfigManager.getJdbcProperties
val df = spark.read.jdbc(jdbcUrl, tableName, props)
```

---

## Architecture et Patterns

### Paradigmes Fonctionnels Respectés

#### **Fonctions PURES**
```scala
// PURE : pas d'effets de bord, même entrée → même sortie
def sourceFromCsv(path: String): DataFrame = {
  spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv(path)
}

// PURE : retour explicite de succès/échec
def writeToCsv(df: DataFrame, destination: String): Boolean = {
  try {
    df.write.mode("overwrite").csv(destination)
    true
  } catch {
    case _: Exception => false
  }
}
```

#### **Immutabilité**
- Tous les DataFrames sont **immutables**
- Les transformations créent de **nouveaux DataFrames**
- Configuration en **lazy val** (évaluation unique)

#### **Séparation des Responsabilités**
```scala
// Reader : SEULEMENT lecture
object Reader {
  def sourceFromCsv(path: String): DataFrame = ...
}

// Writer : SEULEMENT écriture  
object Writer {
  def writeToCsv(df: DataFrame, dest: String): Boolean = ...
}

// Config : SEULEMENT configuration
object ConfigManager {
  lazy val postgresUrl: String = ...
}
```

### Pattern d'Utilisation Recommandé

```scala
object BronzeLayer {
  def run(): Unit = {
    // 1. Lecture avec Reader 
    val restaurantsDF = Reader.sourceFromCsv(ConfigManager.restaurantsCsvPath)
    val ratingsDF = Reader.sourceFromPostgreSQL(ConfigManager.ratingsHistoryTable)
    
    // 2. Transformations 
    val processedDF = restaurantsDF.filter($"score" > 4.0)
    
    // 3. Écriture avec Writer 
    val success = Writer.writeToParquet(processedDF, ConfigManager.bronzePath + "/restaurants")
    
    // 4. Logging du résultat
    if (success) logger.info("Couche Bronze terminée avec succès")
    else logger.error("Échec de la couche Bronze")
  }
}
```

---

## Tests et Validation

### Tests Unitaires Recommandés

```scala
// Test Reader
val testDF = Reader.sourceFromCsv("test-data.csv")
assert(testDF.count() > 0)
assert(testDF.columns.contains("id"))

// Test Writer
val success = Writer.writeToParquet(testDF, "test-output")
assert(success == true)

// Test Config
assert(ConfigManager.postgresUrl.contains("postgresql"))
assert(ConfigManager.bronzePath == "src/data/bronze")
```

### Validation des Formats

```scala
// Validation CSV
val csvDF = Reader.sourceFromCsv("restaurants.csv")
Writer.showStats(csvDF, "Restaurants CSV")

// Validation PostgreSQL
val pgDF = Reader.sourceFromPostgreSQL("restaurant_ratings_history")
Writer.showStats(pgDF, "Historique des Notes")
```

---

## Configuration et Débogage

### Logs Recommandés
```scala
import com.typesafe.scalalogging.LazyLogging

object Reader extends LazyLogging {
  def sourceFromCsv(path: String): DataFrame = {
    logger.info(s"Lecture CSV : $path")
    val df = spark.read.option("header", "true").csv(path)
    logger.info(s"CSV lu : ${df.count()} lignes")
    df
  }
}
```

### Gestion des Erreurs
```scala
def writeToParquet(df: DataFrame, destination: String): Boolean = {
  try {
    df.write.mode("overwrite").parquet(destination)
    logger.info(s"Parquet écrit : $destination")
    true
  } catch {
    case e: Exception =>
      logger.error(s"Erreur d'écriture : ${e.getMessage}")
      false
  }
}
```

---

**Documentation auto-générée - Version 1.0**
