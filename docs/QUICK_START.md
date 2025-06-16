# Guide de Démarrage Rapide

## Configuration en 5 Minutes

### 1. Configuration Initiale 
```bash
git clone [repo-url]
cd EFREI_M1-Functional_Data_Programming-Projet_Final
```

### 2. Allimentation BDD PostgreSQL 
```bash
# Installer les dépendances Python
pip install -r requirements.txt

# Générer les données PostgreSQL (100 restaurants pour test)
python scripts/data_generator.py --sample 100 --validate
```

**Sortie attendue :**
```
Statistiques :
   - Historique des notes : 1 014 enregistrements
   - Performance livraison : 100 enregistrements
   - Campagnes promotionnelles : 186 enregistrements
GÉNÉRATION TERMINÉE AVEC SUCCÈS
```

### 3. Test de Compilation Scala 
```bash
sbt compile
```

**Sortie attendue :**
```
[success] Total time: 5 s, completed
```


### 4. Test PostgreSQL 
Ouvrir VS Code → Extension PostgreSQL → Se connecter à `local-postgres` :
```sql
SELECT COUNT(*) FROM restaurant_ratings_history;
SELECT COUNT(*) FROM delivery_performance;
SELECT COUNT(*) FROM promotional_campaigns;
```

---

## Liste de Vérification

- [ ] **CSV présents** : `restaurants.csv` et `restaurant-menus.csv` à la racine
- [ ] **PostgreSQL** : 3 tables créées avec données
- [ ] **Scala** : Compilation SBT réussie
- [ ] **Structure** : Dossiers `src/data/bronze/silver/gold/` créés
- [ ] **Config** : `application.conf` avec URLs PostgreSQL correctes

---

## Problèmes Courants

### Erreur : Connexion PostgreSQL refusée
```bash
# Vérifier que le conteneur est démarré
# VS Code → Extension PostgreSQL → local-postgres
```

### Erreur : CSV non trouvé
```bash
# Les fichiers CSV doivent être à la racine, pas dans un sous-répertoire
mv chemin/vers/restaurants.csv ./
mv chemin/vers/restaurant-menus.csv ./
```

### Erreur : Échec de compilation SBT
```bash
# Vérifier Java 8 ou 11
java -version

# Nettoyer et recompiler
sbt clean compile
```

---
