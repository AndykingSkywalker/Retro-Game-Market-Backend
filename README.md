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
- **Spring Boot backend** handles domain logic for users, items, carts, and JWT auth
- **Persistence layer** is configured through standard Spring datasource settings
- **CI/CD pipelines** (in progress) validate and automate build/test/deploy workflows
- **GCP integration** (in progress) for hosting, managed services, and operations tooling

---

## Auth and roles

The backend now supports JWT authentication with two roles:

- `CUSTOMER`
- `ADMIN`

### Public behavior

- `POST /api/users` always creates a `CUSTOMER`
- `POST /api/auth/login` returns:
  - `token`
  - `role`
- `GET /api/auth/me` returns the currently authenticated user profile
- `GET /api/items` and `GET /api/items/{id}` are public

### Protected behavior

- **Admins** can create/update/delete items and view all users/carts
- **Admins** can promote a user with `POST /api/users/{id}/promote-admin`
- **Admins** can demote a user with `POST /api/users/{id}/demote-customer`
- **Customers** can view only their own user profile and use only their own cart

### Login response shape

```json
{
  "token": "eyJ...",
  "role": "CUSTOMER"
}
```

### Admin bootstrap

For safety, admins are **not** created through public signup.
Instead, you can bootstrap an admin account via config:

```properties
app.bootstrap.admin.enabled=true
app.bootstrap.admin.username=admin
app.bootstrap.admin.email=admin@example.com
app.bootstrap.admin.password=change-me
```

When enabled, the app creates that admin user on startup if it does not already exist.

> Tip: keep these values in environment-specific config or environment variables outside source control.

---

## Quick start

### Run tests

```zsh
./mvnw test
```

### Start the app

```zsh
./mvnw spring-boot:run
```

### Register a customer

```zsh
curl -X POST http://localhost:8088/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"customer1","email":"customer1@example.com","password":"secret123"}'
```

### Login

```zsh
curl -X POST http://localhost:8088/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"customer1","password":"secret123"}'
```

### Current user (JWT required)

```zsh
curl http://localhost:8088/api/auth/me \
  -H "Authorization: Bearer <token>"
```

### Use JWT in Swagger

- Open `http://localhost:8088/swagger-ui/index.html`
- Click **Authorize** (top-right lock icon)
- Enter `Bearer <token>` using a token from `POST /api/auth/login`
- Use an admin token for admin-only endpoints like `GET /api/users`

### Create an item as an admin

```zsh
curl -X POST http://localhost:8088/api/items \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-token>" \
  -d '{"itemName":"Chrono Trigger","console":"SNES","genre":"RPG","stockLevel":10,"price":79.99,"imageUrl":"https://cdn.example.com/chrono-trigger.jpg","onSale":true}'
```

### Promote a user to admin

```zsh
curl -X POST http://localhost:8088/api/users/2/promote-admin \
  -H "Authorization: Bearer <admin-token>"
```

### Demote a user to customer

```zsh
curl -X POST http://localhost:8088/api/users/2/demote-customer \
  -H "Authorization: Bearer <admin-token>"
```

---

## Repository Structure

```text
retro-game-market/
├── src/
│   ├── main/java/org/example/
│   │   ├── domain/         # Entity/domain models
│   │   ├── repo/           # Data access repositories
│   │   ├── rest/           # REST controllers + DTOs + exception handling
│   │   ├── security/       # JWT auth, authorization, bootstrap admin config
│   │   └── service/        # Business logic
│   ├── main/resources/
│   │   └── application.properties
│   └── test/java/org/example/
│       └── rest/           # Controller and security integration tests
├── pom.xml
└── README.md
```
