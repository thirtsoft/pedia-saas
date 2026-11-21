# pedia-saas

Fondation backend du SaaS pédiatrique pediaSaaS.

## Stack

- Java 21
- Spring Boot 3.5.x
- Spring Data JPA / Hibernate
- PostgreSQL 17
- Flyway
- Spring Security
- Actuator / Micrometer / Prometheus
- JUnit 5 / Mockito / AssertJ
- Testcontainers PostgreSQL
- JaCoCo
- Checkstyle
- SpotBugs
- Sonar
- Docker / Docker Compose

## Pré-requis

- JDK 21
- Maven 3.9+
- Docker + Docker Compose

## Démarrage PostgreSQL

```bash
docker compose up -d postgres
```

## Lancer l'application

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

API de santé :

```text
GET http://localhost:8080/api/actuator/health
```

## Tests

Les tests unitaires sont exécutables sans PostgreSQL.

Le test d'intégration utilise Testcontainers et nécessite Docker :

```bash
mvn clean verify
```

## Sonar

Configurer `SONAR_HOST_URL` et `SONAR_TOKEN`, puis :

```bash
mvn clean verify -Psonar sonar:sonar
```

## Règle d'architecture

Dépendances :

```text
web -> application -> domain
infrastructure -> domain
shared = transversal minimal
```

PostgreSQL est la base officielle du projet MVP. Aucune abstraction pour MongoDB ou Oracle n'est prévue.
