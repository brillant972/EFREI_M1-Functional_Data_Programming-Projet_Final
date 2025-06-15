# 🐍 Scripts Python - Setup PostgreSQL

Ce dossier contient les scripts pour alimenter automatiquement PostgreSQL avec des données complémentaires au dataset Kaggle.

## 📋 **Prérequis**

1. **PostgreSQL** : Container `local-postgres` démarré (via extension VS Code)
2. **Python packages** : Installer avec `pip install -r requirements.txt`
3. **Fichiers CSV** : `restaurants.csv` et `restaurant-menus.csv` dans le répertoire parent

## 🚀 **Utilisation**

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

## 📊 **Ce que fait le script**

1. **🏗️ Setup automatique** : Crée la database `uber_eats_analysis`
2. **📋 Création tables** : Exécute automatiquement `02_create_tables.sql`
3. **📈 Génération données** : Crée des données réalistes pour :
   - `restaurant_ratings_history` (évolution temporelle)
   - `delivery_performance` (métriques livraison)
   - `promotional_campaigns` (campagnes marketing)
4. **✅ Validation** : Statistiques et vérification cohérence

## 🎯 **Résultat attendu**

```
📊 Statistiques des données:
   - Ratings history: 25,340 enregistrements
   - Delivery performance: 3,158 enregistrements  
   - Promotional campaigns: 7,891 enregistrements
```

## 🔧 **Architecture des données générées**

- **Cohérence** : Données liées aux vrais `restaurant_id` du CSV
- **Réalisme** : Patterns basés sur géographie et scores existants
- **Temporalité** : Évolution sur 12 derniers mois
- **Business** : Métriques opérationnelles réalistes

## 🧪 **Test rapide**

```bash
# Test avec 10 restaurants seulement
python data_generator.py --sample 10 --validate
```

Devrait afficher quelque chose comme :
```
🚀 DÉMARRAGE DU GÉNÉRATEUR DE DONNÉES
✅ Database uber_eats_analysis créée
✅ Tables créées avec succès
✅ 10 restaurants traités
📊 Statistiques des données:
   - Ratings history: 89 enregistrements
   - Delivery performance: 10 enregistrements
   - Promotional campaigns: 23 enregistrements
🎉 GÉNÉRATION TERMINÉE AVEC SUCCÈS
```
