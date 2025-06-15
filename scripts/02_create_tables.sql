-- ====================================================================================
-- CRÉATION DES TABLES - UBER EATS RESTAURANT ANALYSIS PROJECT
-- ====================================================================================
-- Ce script crée les tables PostgreSQL pour les données complémentaires
-- au dataset Kaggle (données temporelles et opérationnelles)

-- Table 1: Historique des ratings et avis par restaurant
CREATE TABLE IF NOT EXISTS restaurant_ratings_history (
    id SERIAL PRIMARY KEY,
    restaurant_id INTEGER NOT NULL,
    rating DECIMAL(3,2) CHECK (rating >= 1.0 AND rating <= 5.0),
    review_count INTEGER CHECK (review_count >= 0),
    measurement_date DATE NOT NULL,
    quarter VARCHAR(10),
    year_month VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: Métriques de performance de livraison
CREATE TABLE IF NOT EXISTS delivery_performance (
    id SERIAL PRIMARY KEY,
    restaurant_id INTEGER NOT NULL,
    avg_delivery_time_minutes INTEGER CHECK (avg_delivery_time_minutes > 0),
    delivery_success_rate DECIMAL(5,2) CHECK (delivery_success_rate >= 0 AND delivery_success_rate <= 100),
    peak_hours VARCHAR(100),
    delivery_zone_radius_km DECIMAL(4,2) CHECK (delivery_zone_radius_km > 0),
    last_updated DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 3: Campagnes promotionnelles et marketing
CREATE TABLE IF NOT EXISTS promotional_campaigns (
    id SERIAL PRIMARY KEY,
    restaurant_id INTEGER NOT NULL,
    campaign_name VARCHAR(100) NOT NULL,
    discount_percent DECIMAL(5,2) CHECK (discount_percent >= 0 AND discount_percent <= 100),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    target_audience VARCHAR(50),
    effectiveness_score DECIMAL(5,2) CHECK (effectiveness_score >= 0 AND effectiveness_score <= 100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_campaign_dates CHECK (end_date >= start_date)
);

-- Index pour améliorer les performances des jointures
CREATE INDEX IF NOT EXISTS idx_ratings_restaurant_id ON restaurant_ratings_history(restaurant_id);
CREATE INDEX IF NOT EXISTS idx_ratings_date ON restaurant_ratings_history(measurement_date);
CREATE INDEX IF NOT EXISTS idx_delivery_restaurant_id ON delivery_performance(restaurant_id);
CREATE INDEX IF NOT EXISTS idx_campaigns_restaurant_id ON promotional_campaigns(restaurant_id);
CREATE INDEX IF NOT EXISTS idx_campaigns_dates ON promotional_campaigns(start_date, end_date);
