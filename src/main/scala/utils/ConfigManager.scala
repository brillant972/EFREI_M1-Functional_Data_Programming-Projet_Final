import com.typesafe.config.{Config, ConfigFactory}

/**
 * Gestionnaire de configuration simplifié
 * Lit les paramètres essentiels depuis application.conf
 */
object ConfigManager {

  private val config: Config = ConfigFactory.load()

  // Sources de données
  lazy val restaurantsCsvPath: String = config.getString("data.sources.restaurants")
  lazy val menusCsvPath: String = config.getString("data.sources.menuItems")
  
  // Destinations
  lazy val bronzePath: String = config.getString("data.destinations.bronze")
  lazy val silverPath: String = config.getString("data.destinations.silver")
  lazy val goldPath: String = config.getString("data.destinations.gold")

  // PostgreSQL
  lazy val postgresUrl: String = config.getString("database.url")
  lazy val postgresUser: String = config.getString("database.user")
  lazy val postgresPassword: String = config.getString("database.password")
  lazy val postgresDriver: String = config.getString("database.driver")
  
  // Tables PostgreSQL
  lazy val ratingsHistoryTable: String = config.getString("tables.ratingsHistory")
  lazy val deliveryPerformanceTable: String = config.getString("tables.deliveryPerformance") 
  lazy val promotionalCampaignsTable: String = config.getString("tables.promotionalCampaigns")

  // Spark
  lazy val sparkAppName: String = config.getString("spark.app.name")

  /**
   * Propriétés JDBC pour PostgreSQL
   */
  def getJdbcProperties: java.util.Properties = {
    val properties = new java.util.Properties()
    properties.setProperty("user", postgresUser)
    properties.setProperty("password", postgresPassword)
    properties.setProperty("driver", postgresDriver)
    properties
  }
}
