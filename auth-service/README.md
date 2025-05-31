# 🔑 Auth Service

## 📄 Overview

The `auth-service` is a crucial microservice within the Inventory Management System, responsible for handling user authentication and authorization. It provides the functionality for users to log in and obtain JWT (JSON Web Tokens) for secure access to other services within the system.

## 🛠️ Technologies Used

* ☕ **Java:** Programming language used for the service implementation.
* 🚀 **Spring Boot:** Framework for building the microservice.
* 🛡️ **Spring Security:** Framework for providing authentication and authorization.
* 🌐 **Spring Web:** For building RESTful APIs.
* 💾 **Spring Data JPA:** For database interaction through the `UserCredentialRepo` and `RoleRepository` (if applicable).
* 🔑 **JSON Web Tokens (JWT):** For securely transmitting information between parties as a JSON object. Likely implemented using a library like `jjwt`.
* ⚙️ **MySQL:** Relational database used for storing user credentials (configured in `application.properties`).
* **Maven:** Build automation tool (based on standard Spring Boot project structure).
* 🪵 **Logback:** Logging framework (standard Spring Boot setup).
* 📡 **Eureka Client:** For service registration and discovery within the microservices architecture.

## ✨ Functionality

* **User Authentication (Login):** Verifies user credentials (username/email and password) and, upon successful authentication, generates and returns a JWT.
* **JWT Generation:** Creates JWTs containing user identity and potentially roles/permissions.
* **Custom User Details:** Loads user-specific data from the database using a custom implementation of Spring Security's `UserDetailsService` (`CustomUserDetailsService`).

## 🔗 API Endpoints

* **POST** `/auth/login`: Authenticates a user. Accepts a request containing username/email and password (likely in a `JwtRequest` DTO). Returns a `JwtResponse` containing the JWT.

## 🗃️ Data Models

* **`UserCredentials`:** The JPA entity representing user login information, including username/email and password.
* **`CustomUserDetails`:** A custom implementation of Spring Security's `UserDetails` interface, providing a user object with necessary information for Spring Security.
* **`JwtRequest`:** A DTO (likely) used for the login request, containing username and password.
* **`JwtResponse`:** A DTO (likely) used for the login response, containing the generated JWT.

## ⚙️ Service Dependencies

* **`registry` (Eureka Server):** The service registers itself with the Eureka server for service discovery, as indicated by the `eureka.client.service-url.defaultZone` property in `application.properties`. Other services will likely depend on this service for authentication.

## 🛡️ Security Configuration

The `AuthConfig.java` class likely configures Spring Security, including:

* **`UserDetailsService` Bean:** Configures the `CustomUserDetailsService` to be used for user authentication.
* **Password Encoder:** Defines the algorithm used to encode and verify passwords (e.g., `BCryptPasswordEncoder`).
* **Authentication Manager:** Configures the authentication process.
* **JWT Filter:** A filter (not explicitly in the provided filenames, but a common pattern) that intercepts incoming requests, extracts the JWT, and validates it to authenticate users.
* **SecurityFilterChain:** Defines which API endpoints are public and which require authentication. The `/auth/login` endpoint is likely public.

## ❗ Exception Handling

The service likely handles authentication failures (e.g., invalid credentials) and potentially other security-related exceptions. A custom exception like `InvalidCredentialsException` might be used.

## 🪵 Logging

The service uses `logback` for logging application events and errors (standard Spring Boot setup).

## 💾 Database Configuration

The service is configured to connect to a MySQL database named `auth` (which will be created if it doesn't exist) running on `localhost:3306`. The database credentials (`spring.datasource.username=root`, `spring.datasource.password=root`) are specified in `application.properties`. Hibernate is used as the JPA provider, with the dialect set to `org.hibernate.dialect.MySQL8Dialect` and DDL auto set to `update`. SQL queries are also configured to be shown and formatted in the logs for development purposes.

## ▶️ Running the Service

To run the `auth-service` locally:

1.  Ensure you have Java and Maven installed.
2.  Ensure you have a MySQL instance running on `localhost:3306` with the `auth` database configured.
3.  Navigate to the root directory of the `auth-service`.
4.  Build the project using Maven: `mvn clean install`
5.  Run the application using Spring Boot Maven plugin: `mvn spring-boot:run`

The service will be accessible on port `9898` as configured in the `application.properties`. Ensure the `registry` service is also running for this service to register.

## 📚 API Documentation

API documentation for this service can be accessed via Swagger UI (if configured, typically at `/swagger-ui.html`).

## 🤔 Further Considerations

* **Role-Based Access Control (RBAC):** Implement logic to manage user roles and permissions and enforce authorization based on these roles.
* **Token Refresh Mechanism:** Implement a mechanism to refresh expired JWTs without requiring users to log in again.
* **Password Reset Functionality:** Add features for users to reset their passwords.
* **Security Hardening:** Implement best practices for securing the authentication process and protecting against common web vulnerabilities.
* **Testing:** Add unit and integration tests to ensure the security and reliability of the `auth-service`.
