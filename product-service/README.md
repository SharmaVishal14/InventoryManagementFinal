# Product Service

## Overview

The `product-service` is a core microservice within the Inventory Management System. Its primary responsibility is to manage product-related information, including adding new products, updating existing product details, retrieving product information, and managing stock levels through integration with the `stock-service`. This service interacts with the database to persist and retrieve product data and provides REST API endpoints for other services to access and manage product information.

## Technologies Used

* **Java:** Programming language used for the service implementation.
* **Spring Boot:** Framework for building the microservice.
* **Spring Web:** For building RESTful APIs.
* **Spring Data JPA:** For database interaction through the `ProductRepository`.
* **Spring Cloud OpenFeign:** For declarative HTTP client to communicate with the `stock-service` via the `StockClient`.
* **Lombok:** For reducing boilerplate code in Java classes (implicitly used through annotations).
* **MySQL:** Relational database used for persisting product information (configured in `application.properties`).
* **Maven:** Build automation tool (based on standard Spring Boot project structure).
* **Logback:** Logging framework (configured in `logback.xml`).
* **SpringDoc OpenAPI:** For generating and serving Swagger API documentation.
* **Eureka Client:** For service registration and discovery within the microservices architecture.

## Functionality

The `product-service` provides the following functionalities:

* **Adding a new product:** Allows adding products with details such as name, description, and price. Upon successful creation, it also communicates with the `stock-service` to initialize the stock level for the new product.
* **Retrieving a product by ID:** Enables fetching product details based on its unique identifier.
* **Updating an existing product:** Permits modification of product information.
* **Deleting a product:** Allows removal of a product from the system. This operation also informs the `stock-service` to handle the corresponding stock information.
* **Updating Stock:** Allows for updating the stock level of a product by communicating with the `stock-service`.

## API Endpoints

The service exposes the following REST API endpoints:

* `POST /api/products`: Creates a new product. Accepts a `ProductDto` in the request body. Returns the created `ProductDto`.
* `GET /api/products/{id}`: Retrieves a product by its ID. Returns a `ProductDto`.
* `PUT /api/products/{id}`: Updates an existing product. Accepts a `ProductDto` in the request body. Returns the updated `ProductDto`.
* `DELETE /api/products/{id}`: Deletes a product by its ID. Returns a `ResponseEntity` with an appropriate status.
* `PUT /api/products/stock/{productId}`: Updates the stock level of a product. Accepts a `StockUpdateRequest` in the request body.

## Data Models

The service utilizes the following data models:

* **`Product`:** Represents the product entity stored in the database. It includes fields such as `productId`, `name`, `description`, and `price`.
* **`ProductDto`:** A Data Transfer Object used for transferring product information in API requests and responses.
* **`StockUpdateRequest`:** Represents the request body for updating the stock level of a product, containing the `quantity`.
* **`StockDto`:** A Data Transfer Object representing the stock information received from the `stock-service`.
* **`ErrorDetails`:** A structured object for returning error information in API responses.

## Service Dependencies

This service depends on the following other microservices:

* **`stock-service`:** The `product-service` communicates with the `stock-service` to initialize, update, and manage the stock levels of products. It uses a Feign Client (`StockClient`) for this declarative HTTP communication. The base URL for the `stock-service` is likely configured in the `application.properties`.
* **`registry` (Eureka Server):** The service registers itself with the Eureka server for service discovery, as indicated by the `eureka.client.service-url.defaultZone` property in `application.properties`.

## Exception Handling

The service implements global exception handling using `GlobalExceptionHandler`. It specifically handles `ProductNotFoundException` when a requested product does not exist. Error details, including the timestamp, error message, and a detailed description, are returned in a structured `ErrorDetails` format with an appropriate HTTP status code.

## Logging

The service uses `logback` for logging application events and errors. The `logback.xml` file configures the logging behavior, likely including console appenders and potentially file appenders with different log levels (e.g., INFO, DEBUG, ERROR).

## Database Configuration

The service is configured to connect to a MySQL database named `products` (which will be created if it doesn't exist) running on `localhost:3306`. The database credentials (`spring.datasource.username` and `spring.datasource.password`) are set to `root`. Hibernate is used as the JPA provider, with the dialect set to `MySQLDialect` and DDL auto set to `update`. SQL queries are also configured to be shown and formatted in the logs for development purposes.

## Running the Service

To run the `product-service` locally:

1.  Ensure you have Java and Maven installed.
2.  Ensure you have a MySQL instance running on `localhost:3306` with the specified credentials.
3.  Navigate to the root directory of the `product-service`.
4.  Build the project using Maven: `mvn clean install`
5.  Run the application using Spring Boot Maven plugin: `mvn spring-boot:run`

The service will be accessible on port `8080` (default Spring Boot port, unless otherwise specified in `application.properties`). Ensure the `registry` and `stock-service` are also running for full functionality.

## API Documentation

API documentation for this service can be accessed via Swagger UI at `/swagger-ui.html` once the service is running. This is configured by `springdoc.swagger-ui.path` in the `application.properties`.

## Further Considerations

* **Input Validation:** Consider adding more robust validation using annotations (e.g., `@NotNull`, `@Size`, `@Min`, `@Max`) in the `ProductDto` to ensure data integrity at the API level.
* **Asynchronous Communication:** For stock updates, consider using asynchronous communication (e.g., Kafka, RabbitMQ) to decouple the services and improve resilience.
* **Security:** Implement appropriate security measures (e.g., authentication and authorization using Spring Security) to protect the API endpoints.
* **Testing:** Add unit and integration tests to ensure the reliability and correctness of the `product-service`.
