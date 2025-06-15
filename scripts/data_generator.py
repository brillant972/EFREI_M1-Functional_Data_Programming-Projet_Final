#!/usr/bin/env python3
"""
====================================================================================
DATA GENERATOR - UBER EATS RESTAURANT ANALYSIS PROJECT
====================================================================================

Ce script automatise la création de la database, des tables, et l'insertion
des données réalistes dans PostgreSQL.

Usage:
    python data_generator.py [--sample N] [--validate]
"""

import pandas as pd
import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
from faker import Faker
import random
from datetime import datetime, timedelta
import os
import logging
import argparse

# Configuration logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Configuration PostgreSQL
POSTGRES_CONFIG = {
    'host': 'localhost',
    'port': 54876,
    'user': 'postgres',
    'password': 'postgres'
}

DATABASE_NAME = 'uber_eats_analysis'

def execute_sql_file(cursor, filepath):
    """Exécute un fichier SQL"""
    try:
        with open(filepath, 'r', encoding='utf-8') as file:
            sql_content = file.read()
            
        # Nettoyer et séparer les commandes SQL
        sql_commands = []
        current_command = ""
        
        for line in sql_content.split('\n'):
            line = line.strip()
            if line and not line.startswith('--'):
                current_command += " " + line
                if line.endswith(';'):
                    sql_commands.append(current_command.strip())
                    current_command = ""
        
        # Ajouter la dernière commande si elle n'était pas terminée par ;
        if current_command.strip():
            sql_commands.append(current_command.strip())
            
        for command in sql_commands:
            if command:
                logger.info(f"Exécution: {command[:50]}...")
                cursor.execute(command)
                
        logger.info(f"Fichier {filepath} exécuté avec succès")
        return True
        
    except Exception as e:
        logger.error(f"Erreur lors de l'exécution de {filepath}: {e}")
        return False

def setup_database():
    """Crée la database et les tables automatiquement"""
    logger.info("=== SETUP DATABASE ET TABLES ===")
    
    # Étape 1: Créer la database
    try:
        conn = psycopg2.connect(**POSTGRES_CONFIG)
        conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
        cursor = conn.cursor()
        
        # Vérifier si la database existe déjà
        cursor.execute("SELECT 1 FROM pg_catalog.pg_database WHERE datname = %s", (DATABASE_NAME,))
        exists = cursor.fetchone()
        
        if not exists:
            logger.info(f"Création de la database {DATABASE_NAME}")
            cursor.execute(f'CREATE DATABASE "{DATABASE_NAME}"')
        else:
            logger.info(f"Database {DATABASE_NAME} existe déjà")
            
        cursor.close()
        conn.close()
        
    except Exception as e:
        logger.error(f"Erreur lors de la création de la database: {e}")
        return False
    
    # Étape 2: Créer les tables
    try:
        db_config = POSTGRES_CONFIG.copy()
        db_config['database'] = DATABASE_NAME
        
        conn = psycopg2.connect(**db_config)
        conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
        cursor = conn.cursor()
        
        # Exécuter le script de création des tables
        tables_script = os.path.join(os.path.dirname(__file__), '02_create_tables.sql')
        if execute_sql_file(cursor, tables_script):
            logger.info("Tables créées avec succès")
        else:
            logger.error("Échec de la création des tables")
            return False
            
        cursor.close()
        conn.close()
        return True
        
    except Exception as e:
        logger.error(f"Erreur lors de la création des tables: {e}")
        return False

def load_restaurant_ids(csv_path, sample_size=None):
    """Charge les restaurant_ids depuis le CSV"""
    try:
        logger.info(f"Lecture du fichier restaurants: {csv_path}")
        df = pd.read_csv(csv_path)
        
        restaurant_ids = df['id'].dropna().astype(int).tolist()
        
        if sample_size:
            restaurant_ids = restaurant_ids[:sample_size]
            logger.info(f"Échantillon de {len(restaurant_ids)} restaurants")
        else:
            logger.info(f"Total {len(restaurant_ids)} restaurants trouvés")
            
        return restaurant_ids
        
    except Exception as e:
        logger.error(f"Erreur lors de la lecture du CSV: {e}")
        return []

def generate_ratings_history(restaurant_id, fake):
    """Génère l'historique des ratings pour un restaurant"""
    ratings = []
    base_date = datetime.now() - timedelta(days=365)
    
    # Générer 8-12 mesures sur 12 mois
    num_measurements = random.randint(8, 12)
    
    for i in range(num_measurements):
        measurement_date = base_date + timedelta(days=random.randint(0, 365))
        rating = round(random.uniform(3.5, 5.0), 2)
        review_count = random.randint(10, 500)
        quarter = f"Q{((measurement_date.month - 1) // 3) + 1}-{measurement_date.year}"
        year_month = measurement_date.strftime("%Y-%m")
        
        ratings.append((
            restaurant_id, rating, review_count, measurement_date,
            quarter, year_month
        ))
    
    return ratings

def generate_delivery_performance(restaurant_id, fake):
    """Génère les métriques de performance de livraison"""
    avg_delivery_time = random.randint(15, 45)
    success_rate = round(random.uniform(85.0, 99.0), 2)
    
    # Heures de pointe réalistes
    peak_hours_options = [
        "12:00-13:00,19:00-21:00",
        "11:30-13:30,18:30-20:30",
        "12:00-14:00,19:00-22:00"
    ]
    peak_hours = random.choice(peak_hours_options)
    
    delivery_radius = round(random.uniform(2.5, 15.0), 2)
    last_updated = fake.date_between(start_date='-30d', end_date='today')
    
    return (restaurant_id, avg_delivery_time, success_rate, peak_hours, 
            delivery_radius, last_updated)

def generate_promotional_campaigns(restaurant_id, fake):
    """Génère les campagnes promotionnelles"""
    campaigns = []
    num_campaigns = random.randint(1, 3)
    
    campaign_names = [
        "Summer Sale", "New Customer Welcome", "Weekend Special",
        "Happy Hour", "Lunch Deal", "Holiday Promotion"
    ]
    
    audiences = ["new_customers", "loyal", "all"]
    
    for _ in range(num_campaigns):
        campaign_name = random.choice(campaign_names)
        discount = round(random.uniform(5.0, 30.0), 2)
        
        start_date = fake.date_between(start_date='-90d', end_date='today')
        end_date = fake.date_between(start_date=start_date, end_date='+30d')
        
        target_audience = random.choice(audiences)
        effectiveness = round(random.uniform(15.0, 85.0), 2)
        
        campaigns.append((
            restaurant_id, campaign_name, discount, start_date,
            end_date, target_audience, effectiveness
        ))
    
    return campaigns

def insert_data_batch(cursor, table_name, columns, data_batch):
    """Insère un batch de données"""
    if not data_batch:
        return
        
    placeholders = ','.join(['%s'] * len(columns))
    query = f"INSERT INTO {table_name} ({','.join(columns)}) VALUES ({placeholders})"
    
    cursor.executemany(query, data_batch)
    logger.info(f"Inséré {len(data_batch)} enregistrements dans {table_name}")

def generate_and_insert_data(restaurant_ids, sample_size=None):
    """Génère et insère toutes les données"""
    logger.info("=== GÉNÉRATION ET INSERTION DES DONNÉES ===")
    
    fake = Faker()
    
    try:
        db_config = POSTGRES_CONFIG.copy()
        db_config['database'] = DATABASE_NAME
        
        conn = psycopg2.connect(**db_config)
        cursor = conn.cursor()
        
        # Nettoyer les tables existantes
        cursor.execute("TRUNCATE restaurant_ratings_history, delivery_performance, promotional_campaigns RESTART IDENTITY")
        logger.info("Tables nettoyées")
        
        # Préparer les batches
        ratings_batch = []
        delivery_batch = []
        campaigns_batch = []
        
        restaurants_to_process = restaurant_ids[:sample_size] if sample_size else restaurant_ids
        
        for i, restaurant_id in enumerate(restaurants_to_process):
            if i % 100 == 0:
                logger.info(f"Traitement restaurant {i+1}/{len(restaurants_to_process)}")
            
            # Générer données pour ce restaurant
            ratings_batch.extend(generate_ratings_history(restaurant_id, fake))
            delivery_batch.append(generate_delivery_performance(restaurant_id, fake))
            campaigns_batch.extend(generate_promotional_campaigns(restaurant_id, fake))
            
            # Insérer par batches de 1000
            if len(ratings_batch) >= 1000:
                insert_data_batch(cursor, "restaurant_ratings_history",
                    ["restaurant_id", "rating", "review_count", "measurement_date", "quarter", "year_month"],
                    ratings_batch)
                ratings_batch = []
                
        # Insérer les derniers batches
        if ratings_batch:
            insert_data_batch(cursor, "restaurant_ratings_history",
                ["restaurant_id", "rating", "review_count", "measurement_date", "quarter", "year_month"],
                ratings_batch)
                
        insert_data_batch(cursor, "delivery_performance",
            ["restaurant_id", "avg_delivery_time_minutes", "delivery_success_rate", 
             "peak_hours", "delivery_zone_radius_km", "last_updated"],
            delivery_batch)
            
        insert_data_batch(cursor, "promotional_campaigns",
            ["restaurant_id", "campaign_name", "discount_percent", "start_date",
             "end_date", "target_audience", "effectiveness_score"],
            campaigns_batch)
        
        conn.commit()
        cursor.close()
        conn.close()
        
        logger.info("✅ Toutes les données ont été insérées avec succès")
        return True
        
    except Exception as e:
        logger.error(f"Erreur lors de l'insertion des données: {e}")
        return False

def validate_data():
    """Valide les données insérées"""
    logger.info("=== VALIDATION DES DONNÉES ===")
    
    try:
        db_config = POSTGRES_CONFIG.copy()
        db_config['database'] = DATABASE_NAME
        
        conn = psycopg2.connect(**db_config)
        cursor = conn.cursor()
        
        # Compter les enregistrements
        cursor.execute("SELECT COUNT(*) FROM restaurant_ratings_history")
        ratings_count = cursor.fetchone()[0]
        
        cursor.execute("SELECT COUNT(*) FROM delivery_performance")
        delivery_count = cursor.fetchone()[0]
        
        cursor.execute("SELECT COUNT(*) FROM promotional_campaigns")
        campaigns_count = cursor.fetchone()[0]
        
        logger.info(f"📊 Statistiques des données:")
        logger.info(f"   - Ratings history: {ratings_count:,} enregistrements")
        logger.info(f"   - Delivery performance: {delivery_count:,} enregistrements")
        logger.info(f"   - Promotional campaigns: {campaigns_count:,} enregistrements")
        
        cursor.close()
        conn.close()
        
        return True
        
    except Exception as e:
        logger.error(f"Erreur lors de la validation: {e}")
        return False

def main():
    """Fonction principale"""
    parser = argparse.ArgumentParser(description='Générateur de données PostgreSQL')
    parser.add_argument('--sample', type=int, help='Nombre de restaurants à traiter (pour tests)')
    parser.add_argument('--validate', action='store_true', help='Valider les données après insertion')
    
    args = parser.parse_args()
    
    logger.info("🚀 DÉMARRAGE DU GÉNÉRATEUR DE DONNÉES")
    
    # Étape 1: Setup database et tables
    if not setup_database():
        logger.error("❌ Échec du setup de la database")
        return
      # Étape 2: Charger les restaurant IDs
    csv_path = os.path.join(os.path.dirname(__file__), '..', '..', 'restaurants.csv')
    restaurant_ids = load_restaurant_ids(csv_path, args.sample)
    
    if not restaurant_ids:
        logger.error("❌ Aucun restaurant trouvé dans le CSV")
        return
    
    # Étape 3: Générer et insérer les données
    if not generate_and_insert_data(restaurant_ids, args.sample):
        logger.error("❌ Échec de la génération des données")
        return
    
    # Étape 4: Validation optionnelle
    if args.validate:
        validate_data()
    
    logger.info("🎉 GÉNÉRATION TERMINÉE AVEC SUCCÈS")

if __name__ == "__main__":
    main()
