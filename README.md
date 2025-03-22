# Project README

## Overview

This project is a REST API backend using PostgreSQL. Users must register or log in to access the system. After logging in, a JWT token is provided, which is required for authenticated requests.

It's recommended to use Swagger UI to easily explore API endpoints and data models.

## Technologies

- Java (Spring Boot)
- PostgreSQL
- Spring Security (JWT)
- Swagger UI

## Getting Started

### Database Configuration

Ensure PostgreSQL is installed and configure your database in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Accessing Swagger UI

After running the project, access Swagger at:

```
http://localhost:8080/swagger-ui.html
```

Swagger allows easy testing of API endpoints and exploration of data models.

## User Actions

### Register

Create a new user:

```http
POST /api/auth/register
```

### Login

Log in with an existing user:

```http
POST /api/auth/login
```

Upon successful login, you'll receive a JWT token.

### Using JWT Token

Include the JWT token in HTTP headers for authenticated requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

## Project Constraints

- Records are soft-deleted, not permanently removed.
- Critical actions require `PROJECT_MANAGER` or `TEAM_LEADER` roles.
- Uploaded files are stored in the `uploads` folder.

