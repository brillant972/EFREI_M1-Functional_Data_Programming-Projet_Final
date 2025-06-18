# Journal des Modifications

Toutes les modifications notables de ce projet seront documentées dans ce fichier.

Le format est basé sur [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
et ce projet adhère au [Versioning Sémantique](https://semver.org/spec/v2.0.0.html).

---

## [En cours] - 2025-06-16

### Ajouté
- **Documentation complète du projet**
  - README.md professionnel avec badges technologiques et architecture claire
  - TECHNICAL_DOCUMENTATION.md pour les classes utilitaires
  - QUICK_START.md pour guide de configuration 5 minutes
  - Structure docs/ pour l'organisation

### Modifié
- **Style de documentation** vers format professionnel en français
- **ConfigManager.scala** simplifié pour la lisibilité
- **Scripts Python** adaptés pour fichiers CSV à la racine du projet

---

## [0.3.0] - 2025-06-16

### Ajouté
- **Package utils complet**
  - Reader.scala avec fonctions PURES de lecture (CSV, PostgreSQL, Parquet)
  - Writer.scala avec fonctions PURES d'écriture (CSV, Parquet, JSON)
  - ConfigManager.scala pour configuration centralisée
- **application.conf** configuration complète Spark/PostgreSQL
- **Architecture des répertoires** conforme aux exigences (src/data/bronze/silver/gold)
- **Conventions de nommage** appliquées (répertoires minuscules, fichiers PascalCase)

### Modifié
- **Structure des packages** simplifiée (suppression com.efrei.restaurant)
- **Chemins CSV** adaptés pour fichiers à la racine du projet

### Testé
- **Compilation SBT** : Réussie sans erreurs
- **Configuration** : Lecture application.conf validée
- **PostgreSQL** : Connexion et génération de données OK

---

## [0.2.0] - 2025-06-15

### Ajouté
- **Script Python complet** data_generator.py
  - Génération automatique base de données + tables PostgreSQL
  - Données réalistes pour 3 tables (ratings, delivery, campaigns)
  - Support échantillonnage pour tests (--sample N)
  - Validation automatique avec statistiques
- **Documentation script** README.md avec versions testées
- **Configuration PostgreSQL** adaptée pour port 54876

### Modifié
- **requirements.txt** avec versions exactes testées

### Testé
- **PostgreSQL** : 1 014 ratings + 100 delivery + 186 campaigns générés
- **Script Python** : Fonctionnel avec échantillonnage et validation

---

## [0.1.0] - 2025-06-14

### Ajouté
- **Configuration projet SBT initial**
  - build.sbt avec Spark 3.5.0 et PostgreSQL
  - Structure Scala standard (src/main/scala, src/test/scala)
  - Configuration des dépendances complète
- **Architecture médaillon de base**
  - Répertoires data/bronze/silver/gold
  - Structure .gitignore adaptée
- **Branche develop** comme branche principale
- **Configuration documentation de base**

### Configuration
- **Workflow Git** avec branches feature
- **Conteneur PostgreSQL** local-postgres configuré
- **Jeu de données Kaggle** Uber Eats téléchargé et analysé 

---

## Feuille de Route

### [0.4.0] - Couche Bronze
- [ ] BronzeLayer.scala avec fonction run()
- [ ] Ingestion de données 3 sources (CSV + PostgreSQL)
- [ ] Tests unitaires BronzeLayer
- [ ] Validation end-to-end Bronze

### [0.5.0] - Couche Silver  
- [ ] SilverLayer.scala avec transformations
- [ ] Nettoyage et normalisation des données
- [ ] Jointures complexes restaurants ↔ menus
- [ ] Tests unitaires SilverLayer

### [0.6.0] - Couche Gold
- [ ] GoldLayer.scala avec analytics
- [ ] Agrégations business par région/catégorie
- [ ] Calculs de KPI et métriques finales
- [ ] Tests unitaires GoldLayer

### [1.0.0] - Version Finale
- [ ] RestaurantAnalysisApp.scala complet
- [ ] Tests end-to-end complets
- [ ] Documentation utilisateur finale
- [ ] Optimisations de performance
- [ ] Validation complète des exigences

---

**Format des versions :** 
- **Majeur** : Changements incompatibles
- **Mineur** : Nouvelles fonctionnalités compatibles
- **Correctif** : Corrections de bugs

**Types de changements :**
- `Ajouté` : Nouvelles fonctionnalités
- `Modifié` : Changements aux fonctionnalités existantes  
- `Supprimé` : Fonctionnalités supprimées
- `Corrigé` : Corrections de bugs
- `Sécurité` : Corrections de vulnérabilités
