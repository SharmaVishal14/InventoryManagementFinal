# Stock Service

## Overview

The `stock-service` is a crucial microservice within the Inventory Management System, responsible for managing the inventory levels of products. It allows for tracking the quantity of each product in stock, updating stock levels upon order placement or receipt of goods, and managing reorder levels to prevent stockouts. This service interacts with a database to persist stock information and provides REST API endpoints for other services (primarily the `product-service` and `order-service`) to access and manage stock data. It also communicates with the `product-service` to retrieve product details.

## Technologies Used

* **Java:** Programming language used for the service implementation.
* **Spring Boot:** Framework for building the microservice.
* **Spring Web:** For building RESTful APIs.
* **Spring Data JPA:** For database interaction through the `StockRepository`.
* **Spring Cloud OpenFeign:** For declarative HTTP client to communicate with the `product-service` via the `ProductClient`.
* **Lombok:** For reducing boilerplate code in Java classes (implicitly used through annotations).
* **MySQL:** Relational database used for persisting stock information (configured in `application.properties`).
* **Maven:** Build automation tool (based on standard Spring Boot project structure).
* **Logback:** Logging framework (configured in `logback.xml`).
* **SpringDoc OpenAPI:** For generating and serving Swagger API documentation.
* **Eureka Client:** For service registration and discovery within the microservices architecture.

## Functionality

The `stock-service` provides the following functionalities:

* **Initializing Stock for a New Product:** Called by the `product-service` when a new product is created.
* **Retrieving Stock by Product ID:** Enables fetching the current stock level for a specific product.
* **Updating Stock Levels:** Allows increasing or decreasing the stock quantity of a product.
* **Updating Reorder Level:** Permits modification of the reorder level for a product.
* **Checking Sufficient Stock:** Provides an endpoint to verify if there is enough stock for a given product and quantity.
* **Handling Insufficient Stock:** Throws a custom exception (`InsufficientStockException`) when an operation requires more stock than available.

## API Endpoints

The service exposes the following REST API endpoints:

* `POST /api/stocks/{productId}`: Initializes the stock for a given product ID. Accepts an initial quantity in the request body.
* `GET /api/stocks/{productId}`: Retrieves the stock information for a given product ID. Returns a `StockDto`.
* `PUT /api/stocks/{productId}`: Updates the stock level for a given product ID. Accepts a `StockUpdateRequest` in the request body (containing the quantity change).
* `PUT /api/stocks/reorder-level/{productId}`: Updates the reorder level for a given product ID. Accepts a `ReorderLevelUpdateRequest` in the request body.
* `GET /api/stocks/check/{productId}/{quantity}`: Checks if there is sufficient stock for a given product and quantity. Returns a `ResponseEntity` with a boolean indicating sufficiency.

## Data Models

The service utilizes the following data models:

* **`Stock`:** Represents the stock entity stored in the database. It includes fields such as `productId`, `quantity`, and `reorderLevel`.
* **`StockDto`:** A Data Transfer Object used for transferring stock information in API requests and responses.
* **`StockUpdateRequest`:** Represents the request body for updating the stock level of a product, containing the `quantity`.
* **`ReorderLevelUpdateRequest`:** Represents the request body for updating the reorder level of a product, containing the `reorderLevel`.
* **`ProductDto`:** A Data Transfer Object representing the product information received from the `product-service`.
* **`ErrorResponse`:** A structured object for returning error information in API responses.

## Service Dependencies

This service depends on the following other microservices:

* **`product-service`:** The `stock-service` communicates with the `product-service` to retrieve product details (e.g., to ensure a product exists before initializing stock). It uses a Feign Client (`ProductClient`) for this declarative HTTP communication. The base URL for the `product-service` is likely configured in the `application.properties`.
* **`registry` (Eureka Server):** The service registers itself with the Eureka server for service discovery, as indicated by the `eureka.client.service-url.defaultZone` property in `application.properties`.

## Exception Handling

The service implements global exception handling using `GlobalExceptionHandler`. It specifically handles:

* **`StockNotFoundException`:** When stock information for a requested product does not exist.
* **`InsufficientStockException`:** When an operation attempts to reduce the stock below zero.

Error details are returned in a structured `ErrorResponse` format, including the timestamp, error message, and a detailed description, with an appropriate HTTP status code.

## Logging

The service uses `logback` for logging application events and errors. The `logback.xml` file configures logging to both a file (`stock-service.log`) with daily rolling and a history of 7 days, and to the console. The root log level is set to `INFO`, while the logger specifically for the `com.cts.inventorymanagement.stock-service` package is set to `DEBUG`, providing more detailed logs for this service. The log messages include timestamps, thread information, log level, logger name, and the actual message.

## Database Configuration

The service is configured to connect to a MySQL database named `stocks` (which will be created if it doesn't exist) running on `localhost:3306`. The database credentials (`spring.datasource.username` and `spring.datasource.password`) are set to `root`. Hibernate is used as the JPA provider, with the dialect set to `MySQLDialect` and DDL auto set to `update`. SQL queries are also configured to be shown and formatted in the logs for development purposes.

## Running the Service

To run the `stock-service` locally:

1.  Ensure you have Java and Maven installed.
2.  Ensure you have a MySQL instance running on `localhost:3306` with the specified credentials.
3.  Navigate to the root directory of the `stock-service`.
4.  Build the project using Maven: `mvn clean install`
5.  Run the application using Spring Boot Maven plugin: `mvn spring-boot:run`

The service will be accessible on port `5004` as configured in the `application.properties`. Ensure the `registry` and `product-service` are also running for full functionality.

## API Documentation

API documentation for this service can be accessed via Swagger UI at `/swagger-ui.html` once the service is running. This is configured by `springdoc.swagger-ui.path` in the `application.properties`.

## Further Considerations

* **Data Validation:** Implement input validation for API requests (e.g., using `@Valid` annotation and Bean Validation) to ensure data integrity.
* **Concurrency Control:** Consider implementing optimistic or pessimistic locking mechanisms if concurrent updates to stock levels become a concern.
* **Integration Events:** For significant stock changes, consider publishing integration events (e.g., using Kafka or RabbitMQ) for other interested services to consume.
* **Testing:** Add unit and integration tests to ensure the reliability and correctness of the `stock-service`.
