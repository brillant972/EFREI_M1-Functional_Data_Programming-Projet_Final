# Scripts Python - Setup PostgreSQL

Ce dossier contient les scripts pour alimenter automatiquement PostgreSQL avec des données complémentaires au dataset Kaggle.

## **Prérequis**

1. **PostgreSQL** : Container `local-postgres` démarré (via extension VS Code, port 54876)
2. **Python 3.7+** avec packages installés :
   ```bash
   pip install -r requirements.txt
   ```
   **Versions testées :**
   - `pandas==1.3.5`
   - `psycopg2-binary==2.9.9` 
   - `Faker==18.13.0`

3. **Fichiers CSV** : `restaurants.csv` et `restaurant-menus.csv` à la racine du projet

## **Utilisation**

### **Option 1 : Exécution complète**
```bash
# Tout automatique : database + tables + données
python data_generator.py
```

### **Option 2 : Test avec échantillon**
```bash
# Test avec seulement 100 restaurants
python data_generator.py --sample 100 --validate
```

### **Option 3 : Validation seulement**
```bash
# Juste valider les données existantes
python data_generator.py --validate
```

## **Ce que fait le script**

1. **Setup automatique** : Crée la database `uber_eats_analysis`
2. **Création tables** : Exécute automatiquement `02_create_tables.sql`
3. **Génération données** : Crée des données réalistes pour :
   - `restaurant_ratings_history` (évolution temporelle)
   - `delivery_performance` (métriques livraison)
   - `promotional_campaigns` (campagnes marketing)
4. **Validation** : Statistiques et vérification cohérence

## **Résultat attendu**

```
Statistiques des données:
   - Ratings history: 25,340 enregistrements
   - Delivery performance: 3,158 enregistrements  
   - Promotional campaigns: 7,891 enregistrements
```

## **Architecture des données générées**

- **Cohérence** : Données liées aux vrais `restaurant_id` du CSV
- **Réalisme** : Patterns basés sur géographie et scores existants
- **Temporalité** : Évolution sur 12 derniers mois
- **Business** : Métriques opérationnelles réalistes

## **Test rapide**

```bash
# Test avec 10 restaurants seulement
python data_generator.py --sample 10 --validate
```

## **Notes importantes**

- Le script détecte automatiquement le port PostgreSQL (54876 par défaut)
- Les données sont générées de manière cohérente avec les restaurant_id existants
- Chaque exécution nettoie et recrée les données pour éviter les doublons
- Compatible avec les paradigmes fonctionnels Scala pour le pipeline principal

## 📁 **Structure attendue du projet**

```
EFREI_M1-Functional_Data_Programming-Projet_Final/
├── restaurants.csv                    # ⬅️ À la racine !
├── restaurant-menus.csv               # ⬅️ À la racine !
├── scripts/
│   ├── data_generator.py             # Script à exécuter
│   ├── requirements.txt
│   └── README.md                     # Ce fichier
└── src/data/
    ├── bronze/                       # Données générées ici
    ├── silver/
    └── gold/
```

**⚠️ Important** : Les fichiers CSV doivent être à la racine du projet pour que le script les trouve automatiquement.

