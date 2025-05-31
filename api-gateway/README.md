# 🚪 API Gateway

## 📄 Overview

The API Gateway is the single entry point for all client requests to the Inventory Management System. It acts as a reverse proxy, routing requests to the appropriate backend microservices. It also handles cross-cutting concerns such as authentication, authorization, logging, and potentially rate limiting.

## 🛠️ Technologies Used

* ☕ **Java:** Programming language used for the service implementation.
* 🚀 **Spring Cloud Gateway:** Provides a simple and effective way to route API requests and handle cross-cutting concerns.
* 🛡️ **Spring Security:** Integrated for securing the gateway and enforcing authentication and authorization.
* 🔑 **JSON Web Tokens (JWT):** Used for verifying the authenticity and integrity of client requests.
* ⚙️ **Maven:** Build automation tool (configured in `pom.xml`).
* 🪵 **Spring Cloud Starter Sleuth:** For distributed tracing, providing request IDs across services.
* 🚦 **Spring Cloud Starter LoadBalancer:** For client-side load balancing of requests to service instances.
* 📄 **application.properties:** Configuration file for routing rules, server port, and other gateway settings.
* 📡 **Eureka Client:** For discovering and interacting with backend services registered with the Eureka Server.

## ✨ Functionality

* **Request Routing:** Routes incoming requests to the appropriate backend microservices based on defined rules in `application.properties`.
* **Authentication:** Implements a custom `AuthenticationFilter` to intercept incoming requests and validate the JWT present in the `Authorization` header.
* **Authorization:** Determines if the authenticated user has the necessary permissions to access the requested resource, likely based on the JWT claims and the route configuration in `RouteValidator`.
* **JWT Validation:** Utilizes the `JwtUtil` class to verify the signature and validity of JWTs.
* **Logging:** Configures logging for the gateway using `LoggingConfig`.
* **Distributed Tracing:** Leverages Spring Cloud Sleuth to track requests as they flow through the different microservices.
* **Load Balancing:** Uses Spring Cloud LoadBalancer to distribute traffic across multiple instances of the same service.

## ⚙️ Service Dependencies

* **`registry` (Eureka Server):** The API Gateway discovers the locations of backend services (like `product-service`, `order-service`, etc.) from the Eureka Server.
* **`auth-service` (Implicit):** While not directly calling the `auth-service` in the request path, the gateway relies on the JWTs issued by the `auth-service` for authentication and authorization.

## 🔗 Key Components

* **`ApiGatewayApplication.java`:** The main entry point for the Spring Cloud Gateway application, enabling gateway functionality and Eureka client.
* **`application.properties`:** Contains the routing definitions, mapping paths to service IDs, and configuring the server port.
* **`AuthenticationFilter.java`:** A custom `GlobalFilter` that intercepts requests, checks for the `Authorization` header containing a JWT, validates the token using `JwtUtil`, and potentially sets authentication context.
* **`JwtUtil.java`:** Provides utility methods for validating JWTs, extracting claims, and checking token expiration.
* **`RouteValidator.java`:** Contains logic to define public (non-secured) API endpoints that do not require JWT authentication.
* **`AppConfig.java`:** Likely contains bean definitions for custom configurations, such as the `AuthenticationFilter` and `RouteValidator`.
* **`LoggingConfig.java`:** Configures the logging behavior for the API Gateway.
* **`pom.xml`:** Defines the project dependencies, including `spring-cloud-starter-gateway`, `spring-cloud-starter-netflix-eureka-client`, `spring-boot-starter-security`, `spring-cloud-starter-sleuth`, and `spring-cloud-starter-loadbalancer`.

## 🛡️ Security

The API Gateway implements a security mechanism based on JWTs. Incoming requests are authenticated by verifying the JWT in the `Authorization` header. Only requests with a valid JWT for non-public routes will be forwarded to the backend services.

## 🪵 Logging

The gateway is configured for logging using `LoggingConfig`, providing insights into request handling and potential errors. Spring Cloud Sleuth provides request tracing for better debugging in a distributed environment.

## ▶️ Running the Service

To run the API Gateway locally:

1.  Ensure you have Java and Maven installed.
2.  Ensure the Eureka Server (`registry`) and the `auth-service` are running.
3.  Navigate to the root directory of the API Gateway project.
4.  Build the project using Maven: `mvn clean install`
5.  Run the application using Spring Boot Maven plugin: `mvn spring-boot:run`

The API Gateway will be accessible on the port defined in `application.properties` (typically 8080). All client requests to the Inventory Management System should be directed to this port.

## ⚙️ Configuration (application.properties)

```properties
# Gateway Server Configuration
spring.application.name=api-gateway
server.port=9090

# Eureka Client Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.fetch-registry=true
eureka.client.register-with-eureka=true

# Gateway Routes Configuration
spring.cloud.gateway.routes[0].id=product-service
spring.cloud.gateway.routes[0].uri=lb://product-service 
spring.cloud.gateway.routes[0].predicates[0]=Path=/products/**

spring.cloud.gateway.routes[1].id=order-service
spring.cloud.gateway.routes[1].uri=lb://order-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/orders/**

spring.cloud.gateway.routes[2].id=stock-service
spring.cloud.gateway.routes[2].uri=lb://stock-service
spring.cloud.gateway.routes[2].predicates[0]=Path=/stocks/**
spring.cloud.gateway.routes[2].filters[0]=AuthenticationFilter

spring.cloud.gateway.routes[3].id=purchase-service
spring.cloud.gateway.routes[3].uri=lb://purchase-service
spring.cloud.gateway.routes[3].predicates[0]=Path=/purchases/**
spring.cloud.gateway.routes[3].filters[0]=AuthenticationFilter

spring.cloud.gateway.routes[4].id=supplier-service
spring.cloud.gateway.routes[4].uri=lb://supplier-service
spring.cloud.gateway.routes[4].predicates[0]=Path=/suppliers/**
spring.cloud.gateway.routes[4].filters[0]=AuthenticationFilter

spring.cloud.gateway.routes[5].id=auth-service
spring.cloud.gateway.routes[5].uri=lb://AUTH-SERVICE
spring.cloud.gateway.routes[5].predicates[0]=Path=/auth/**

# Logging for Debugging
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG

spring.main.web-application-type=reactive

spring.cloud.gateway.discovery.locator.enabled: true
