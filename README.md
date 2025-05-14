# 🌱 DevHarvest - Plateforme Agricole Complète (JavaFX + Symfony)

## 🧭 Description

**DevHarvest** est une solution complète de gestion agricole combinant :
- une **application desktop JavaFX** pour les agriculteurs, les techniciens et les administrateurs
- une **application web Symfony** avec interfaces d’administration, API REST et espace client

Elle facilite la gestion des ressources agricoles, la réservation de machines, la communication via forum, et la vente de produits locaux.

---

## 🎯 Fonctionnalités principales

### 🖥️ Application Desktop (JavaFX)
- Authentification & rôles (admin, agriculteur, client, technicien)
- 📦 Gestion des **produits** (CRUD, image, stock, mise à jour en ligne)
- 🛒 Gestion du **panier** et des **commandes** (par client ou admin)
- 🚜 Gestion des **machines agricoles** (ajout, location, état, prix)
- 🌾 Gestion des **cultures** (recommandation selon type de sol & saison)
- 🧭 Gestion des **zones et parcelles agricoles** avec carte Mapbox
- 🏠 Gestion des **granges** liées aux zones
- 💬 Gestion d’un **forum** interne (posts, likes, commentaires)
- 🔔 Notifications intelligentes (parcelles à renouveler, météo)
- 📈 Statistiques et analyses (par produit, par culture, etc.)
- 🤖 Intégration d’un chatbot (IA) et APIs externes (météo, devise, news)

### 🌐 Application Web (Symfony)
- Interface d’administration (CRUD utilisateurs, produits, machines…)
- API REST pour la communication avec JavaFX
- Système de rôles et sécurité via Symfony Security
- Pages web pour clients (consultation, réservation, achat)
- Upload d’images (produits, zones, machines, etc.)
- Système de notifications et tableau de bord

---

## 🧰 Technologies utilisées

| Côté | Technologie |
|------|-------------|
| Backend | PHP 8+, Symfony 6, Doctrine ORM, MySQL |
| Frontend | Java 17+, JavaFX, SceneBuilder |
| APIs externes | OpenWeather, NewsAPI, CurrencyAPI, GPT |
| Sécurité | Symfony Security, JWT (si utilisé), Hashing |
| Outils | Composer, Maven, Git, Postman, SceneBuilder |

---

## ⚙️ Installation

### 1️⃣ Backend Symfony (Web + API)
```bash
cd backend/
composer install
cp .env .env.local
# Modifier la DB dans .env.local (ex: mysql://user:pass@127.0.0.1:3306/devharvest)
php bin/console doctrine:database:create
php bin/console doctrine:migrations:migrate
symfony server:start
