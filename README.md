# ProductScanApp

Application Android permettant de rechercher et scanner des produits afin de consulter leurs informations essentielles, leur NutriScore et de trouver de meilleures alternatives.

## Fonctionnalités

- Recherche d'un produit par code-barres.
- Scan de code-barres avec la caméra.
- Recherche paginée par catégorie.
- Tri des résultats par NutriScore, du meilleur au moins bon.
- Chargement automatique de la page suivante en bas de liste.
- Affichage du nom, de la marque, de l'image et du NutriScore.
- Historique local des produits consultés.
- Ajout et suppression de produits favoris.
- Recommandation d'une meilleure alternative pour les produits classés D ou E.
- Partage d'une fiche produit avec un lien profond.
- Synchronisation périodique des favoris.
- Widget d'écran d'accueil affichant le dernier produit consulté.
- Prise en charge des aliments, cosmétiques et produits pour animaux.

## Sources de données

L'application utilise les services Open Facts :

- Open Food Facts pour les produits alimentaires.
- Open Beauty Facts pour les produits cosmétiques.
- Open Pet Food Facts pour les produits destinés aux animaux.

La recherche par code-barres utilise l'option `product_type=all` afin de rechercher un produit dans les différentes bases compatibles.

## Technologies

- Kotlin

## Prérequis

- Android Studio
- JDK 17
- Android SDK 36
- Appareil ou émulateur Android 6.0 minimum (`minSdk 23`)
- Connexion Internet
- Caméra pour tester le scanner

## Installation

1. Cloner ou ouvrir le projet dans Android Studio.
2. Lancer **Sync Project with Gradle Files**.
3. Sélectionner la configuration `app`.
4. Démarrer un émulateur ou connecter un téléphone Android.
5. Cliquer sur **Run**.

L'APK de développement est généré dans :

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Utilisation

### Recherche par code-barres

Dans l'onglet **Produit**, sélectionner le mode code-barres, saisir un code puis lancer la recherche.

### Recherche par catégorie

Dans l'onglet **Produit**, sélectionner le mode catégorie puis saisir, par exemple :

```text
chocolate-spreads
chocolate
pastas
yogurts
cheeses
```

Les résultats sont triés par NutriScore. Faire défiler la liste jusqu'en bas charge automatiquement la page suivante.

### Scanner

Ouvrir l'onglet **Scanner**, autoriser la caméra et placer le code-barres dans le cadre. La fiche apparaît après détection.

### Favoris

Depuis une fiche produit, appuyer sur l'étoile pour ajouter ou retirer le produit des favoris.

### Recommandations

Lorsqu'un produit alimentaire possède un NutriScore D ou E, l'application recherche une alternative de la même catégorie avec un meilleur NutriScore.

### Widget

1. Effectuer au moins une recherche ou un scan.
2. Revenir à l'écran d'accueil Android.
3. Faire un appui long puis ouvrir la liste des widgets.
4. Ajouter le widget `ProductScanApp`.

Le widget affiche le dernier produit consulté, son image et son NutriScore. S'il n'existe aucun produit, il affiche **Aucun scan récent**. Un appui sur le widget ouvre l'application sur le scanner.

## Codes-barres de test

| Produit               | Code-barres | Source attendue |
|-----------------------|---:|---|
| Nutella               | `3017624010701` | Open Food Facts |
| Liquide vaisselle     | `3600523708857` | Open Beauty Facts |
| Croquettes pour chien | `5010394984577` | Open Pet Food Facts |

Les données peuvent évoluer dans les bases Open Facts,les produits non alimentaires ne possèdent pas de NutriScore.

## Architecture

```text
app/src/main/java/com/example/productscanapp/
|-- data/
|   |-- local/          # Room, entités, DAO et mappers
|   |-- remote/         # API Retrofit et DTO
|   `-- sync/           # Synchronisation WorkManager
|-- di/                 # Modules Hilt
|-- domain/             # Modèles et contrats des repositories
`-- ui/
    |-- product/        # Recherche et fiche produit
    |-- scan/           # Scanner CameraX et ML Kit
    |-- history/        # Historique
    |-- favorite/       # Favoris
    |-- recommendation/ # Alternatives recommandées
    |-- widget/         # Widget Android
    `-- main/           # Navigation principale
```

## Contributeurs

- Nadia
- Victoria
- Juliano

