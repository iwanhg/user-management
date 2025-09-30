# User Management Service

A lightweight, modern Java back-end service for user management, built with Spring Boot. This project provides a solid foundation for a RESTful API with user authentication and database integration.

## Features

- **Framework**: Spring Boot 3.2.5
- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: MySQL with Flyway for schema migrations.
- **Authentication**: JWT-based authentication.
- **Authorization**: Role-based access control (`USER`, `ADMIN`).
- **API Documentation**: Swagger/OpenAPI v3, automatically generated with `springdoc-openapi`.
- **Architecture**: Clean, layered architecture (controller, service, repository).
- **Logging**: Daily rolling log files configured with Logback.

## Prerequisites

Before you begin, ensure you have the following installed:
- JDK 21
- Apache Maven
- MySQL Server

## Setup & Configuration

Follow these steps to get the application running on your local machine.

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd user-management-service
```

### 2. Database Setup

1.  Start your MySQL server.
2.  Connect to MySQL and create a new database for the service.

    ```sql
    CREATE DATABASE user_db;
    ```

### 3. Configure Application Properties

1.  Open the `src/main/resources/application.properties` file.
2.  Update the datasource properties with your MySQL username and password.

    ```properties
    spring.datasource.username=your_mysql_username
    spring.datasource.password=your_mysql_password
    ```

3.  (Optional) You can also change the default administrator credentials that are created on the first run.

    ```properties
    app.default-admin.username=admin
    app.default-admin.password=adminpassword
    ```

## Running the Application

You can run the application using the Maven wrapper included in the project.

-   On macOS/Linux:
    ```bash
    ./mvnw spring-boot:run
    ```
-   On Windows:
    ```bash
    mvnw.cmd spring-boot:run
    ```

The application will start on `http://localhost:8080`. On the first run, Flyway will automatically execute the migration scripts to create the `users` table, and a default admin user will be created.

## Project Structure

The project follows a standard layered architecture to separate concerns.

```
src/main/
├── java/com/example/usermanagement/
│   ├── config/        # Spring Security, OpenAPI, DataInitializer
│   ├── controller/    # REST API endpoints (AuthController, UserController)
│   ├── dto/           # Data Transfer Objects for API requests/responses
│   ├── entity/        # JPA entities (User, Role)
│   ├── exception/     # Global and custom exception handlers
│   ├── repository/    # Spring Data JPA repositories
│   └── service/       # Business logic (AuthService, JwtService, UserService)
└── resources/
    ├── db/migration/  # Flyway SQL migration scripts
    ├── application.properties
    └── logback-spring.xml
```

## How to Use the API

### 1. API Documentation

Once the application is running, you can access the interactive Swagger UI documentation at:

**http://localhost:8080/swagger-ui.html**

This UI allows you to explore and test all available API endpoints.

### 2. Authentication Flow

1.  **Sign In**: Send a `POST` request to `/api/auth/signin` with the credentials of a user (e.g., the default admin user).

    ```json
    {
      "username": "admin",
      "password": "adminpassword"
    }
    ```

2.  **Receive JWT**: The response will contain a JWT access token.

    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "authType": "Bearer",
      "expiresIn": 3600000
    }
    ```

3.  **Access Secured Endpoints**: To access protected endpoints (e.g., `GET /api/users`), include the token in the `Authorization` header.

    `Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...`

    You can do this easily in the Swagger UI by clicking the "Authorize" button and pasting your token.