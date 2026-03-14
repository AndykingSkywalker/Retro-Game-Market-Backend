# Retro Game Market (Full Stack)

A full-stack marketplace application for retro video games.

This project combines:

- **Frontend:** Next.js
- **Backend:** Java Spring Boot (REST API)
- **Database:** Configured via Spring datasource environment variables
- **DevOps focus:** CI/CD, cloud deployment, and GCP learning in public

---

## Project Goals

This project is both a product build and a skills journey.

- Build a clean, modern full-stack marketplace experience
- Practice production-style backend API development with Spring Boot
- Deepen hands-on DevOps capabilities (automation, environments, deployment)
- Explore and implement GCP services incrementally
- Build a repeatable CI/CD pipeline from commit to deployment

---

## Architecture Overview

- **Next.js frontend** consumes REST endpoints exposed by Spring Boot
- **Spring Boot backend** handles domain logic for users, items, and carts
- **Persistence layer** is configured through standard Spring datasource settings
- **CI/CD pipelines** (in progress) validate and automate build/test/deploy workflows
- **GCP integration** (in progress) for hosting, managed services, and operations tooling

---

## Repository Structure

```text
retro-game-market/
├── src/
│   ├── main/java/org/example/
│   │   ├── domain/         # Entity/domain models
│   │   ├── repo/           # Data access repositories
│   │   ├── rest/           # REST controllers + DTOs + exception handling
│   │   └── service/        # Business logic
│   ├── main/resources/
│   │   └── application.properties
│   └── test/java/org/example/
│       └── rest/           # Controller tests
├── pom.xml
└── README.md
