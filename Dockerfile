# Étape 1 : Utiliser OpenJDK 17 comme base
FROM eclipse-temurin:17

# Étape 2 : Ajouter le fichier JAR au conteneur
COPY target/TenderFlow-0.0.1-SNAPSHOT.jar app.jar

# Étape 3 : Exposer le port 8090
EXPOSE 8090

# Étape 4 : Commande pour exécuter l’application
ENTRYPOINT ["java", "-jar", "/app.jar"]