# Hackify

## Description

Hackify est une application de bureau développée en JavaFX qui permet d’organiser, gérer et suivre des hackathons de manière collaborative et structurée.

### Objectif 
Faciliter l'organisation des hackathons pour les organisateurs, offrir un espace clair pour les participants, et permettre une évaluation juste via un jury.
### Problème résolu  
L'absence d'une plateforme unifiée pour la gestion complète de hackathons, du lancement à l'évaluation.
### Fonctionnalités principales 
  - Gestion des hackathons par les organisateurs.
  - Inscriptions des participants.
  - Évaluation des projets par un jury avec attribution de notes.
  - Ajout de ressources et accompagnement par des coachs.
  - Notifications par email (acceptation/refus).

### Contexte du Projet
Ce projet a été réalisé dans le cadre d’un projet d’étude à Esprit Engineering School pour l’année académique 2024/2025, dans le but d’appliquer des compétences avancées en Java, gestion de base de données et design d’interface utilisateur avec JavaFX.

## Table des matières

- [Installation](#installation)
- [Utilisation](#utilisation)
- [Contributions](#contributions)
- [Mots-clés](#mots-clés)

## Installation

1. Clonez le repository :

```bash
git clone https://github.com/fatenke/hackify_javafx
```
2. Ouvrez le projet dans IntelliJ IDEA ou NetBeans

3. Assurez-vous que les éléments suivants sont installés :

   -Java JDK 17+

   -Scene Builder

   -Maven (ou utilisez Gradle si configuré)

4. Importez le projet en tant que projet Maven

5. Configurez la base de données dans application.properties ou via un fichier de configuration Java selon votre implémentation.

6. Créez la base de données MySQL (si elle n'existe pas encore) :

   -Utilisez phpMyAdmin ou exécutez :

```bash
CREATE DATABASE hackify;
```
7. Lancez l’application depuis votre IDE (Main.java)


## Utilisation

### Installation de Java JDK
Téléchargez la dernière version LTS du JDK (17 ou plus recommandé) :
https://www.oracle.com/java/technologies/javase-jdk17-downloads.html

Vérifiez l'installation :

```bash
java -version
```
### Installation de Scene Builder
Téléchargez Scene Builder :
https://gluonhq.com/products/scene-builder/

Intégrez Scene Builder avec IntelliJ :

File > Settings > Languages & Frameworks > JavaFX > Scene Builder Path

### Installation de MySQL
Téléchargez MySQL : https://dev.mysql.com/downloads/

Créez une base de données : hackify

Configurez les identifiants dans le fichier de configuration (ou fichier .env, .properties, etc.)

### Installation de Maven
Téléchargez Maven : https://maven.apache.org/download.cgi

Vérifiez l’installation :
```bash
mvn -v
```

## Fonctionnalités

- Authentification des utilisateurs (organisateur, participant, coach, jury)
- Gestion des rôles et des permissions
- Création et édition de hackathons
- Inscription aux hackathons
- Soumission de projets par les participants
- Évaluation des projets par un jury avec notation
- Ajout de ressources par les coachs pour les projets

## Contributions

1. **Fork le projet**: Allez sur la page Github du projet et cliquez sur le bouton **Fork** dans le coin supérieur droit pour créer une copie du projet dans votre propre compte Github.
2. **Clonez votre fork**: Clonez le fork sur votre machine locale:
bash
git clone https://github.com/fatenke/Hackify

3. **Créez une nouvelle branche**
bash
git checkout -b nomdubranche

4. **Committer aprés modifications pour le enregistrer**
bash
git add . 
git commit -m 'Ajout de la fonctionnalité x'

5. **Pousser vos modifications**
bash
git push origin nomdubranche

4. **Soumettez une Pull Request**
bash
git pull origin nomdubranche


## Mots-clés
JavaFX
Scene Builder
MVC
JDBC
MySQL
Maven
Java 17+
IntelliJ IDEA
FXML
FXML Controller
OpenJFX
