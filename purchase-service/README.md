# Purchase Service

## Overview

The `purchase-service` is a critical microservice within the Inventory Management System, responsible for managing the procurement of products from suppliers. It handles the creation, tracking, and management of purchase orders, ensuring that inventory levels can be replenished efficiently. This service interacts with a database to persist purchase order information and provides REST API endpoints for managing the purchasing process.

## Technologies Used

* **Java:** Programming language used for the service implementation.
* **Spring Boot:** Framework for building the microservice.
* **Spring Web:** For building RESTful APIs.
* **Spring Data JPA:** For database interaction through the `PurchaseOrderRepository`.
* **Lombok:** For reducing boilerplate code in Java classes.
* **MySQL:** Relational database used for persisting purchase order information (configured in `application.properties`).
* **Maven:** Build automation tool (configured in `pom.xml`).
* **Logback:** Logging framework (standard Spring Boot setup, often configured via `logback.xml`).
* **SpringDoc OpenAPI:** For generating and serving Swagger API documentation.
* **Eureka Client:** For service registration and discovery within the microservices architecture.
* **Spring Boot Starter Validation:** For declarative validation of request bodies.
* **Spring Boot Starter Actuator:** For monitoring and managing the application.

## Functionality

The `purchase-service` provides the following functionalities:

* **Creating a new purchase order:** Allows the creation of purchase orders specifying the supplier, product, quantity, order date, and expected delivery date.
* **Retrieving purchase orders:** Supports fetching purchase orders by ID, by supplier, by product, by status, and within a specific date range.
* **Updating purchase order status:** Enables changing the status of a purchase order (e.g., from PENDING to DELIVERED or CANCELLED).
* **Deleting a purchase order:** Allows the removal of a purchase order from the system.

## API Endpoints

The service exposes the following REST API endpoints:

* `POST /purchase-orders`: Creates a new purchase order. Accepts a `PurchaseOrderRequest` in the request body. Returns the created `PurchaseOrderDto`.
* `GET /purchase-orders/{id}`: Retrieves a purchase order by its ID. Returns a `PurchaseOrderDto`.
* `GET /purchase-orders`: Retrieves all purchase orders. Returns a list of `PurchaseOrderDto`.
* `GET /purchase-orders/supplier/{supplierId}`: Retrieves purchase orders for a specific supplier. Returns a list of `PurchaseOrderDto`.
* `GET /purchase-orders/product/{productId}`: Retrieves purchase orders for a specific product. Returns a list of `PurchaseOrderDto`.
* `GET /purchase-orders/status/{status}`: Retrieves purchase orders by their status (e.g., `PENDING`, `DELIVERED`, `CANCELLED`). Returns a list of `PurchaseOrderDto`.
* `GET /purchase-orders/date-range`: Retrieves purchase orders within a specified date range. Requires `startDate` and `endDate` as request parameters (format: `YYYY-MM-DD`). Returns a list of `PurchaseOrderDto`.
* `PATCH /purchase-orders/{id}/status`: Updates the status of a purchase order. Accepts the new `OrderStatus` as a request parameter. Returns the updated `PurchaseOrderDto`.
* `DELETE /purchase-orders/{id}`: Deletes a purchase order by its ID. Returns `HTTP 204 No Content` on successful deletion.

## Data Models

The service utilizes the following data models:

* **`PurchaseOrder`:** The JPA entity representing a purchase order in the database, including `id`, `supplierId`, `productId`, `quantity`, `orderDate`, `deliveryDate`, and `status`.
* **`PurchaseOrderRequest`:** A DTO used for creating new purchase orders. It includes validation annotations (`@NotNull`, `@Positive`, `@Min`, `@FutureOrPresent`, `@Future`).
* **`PurchaseOrderDto`:** A DTO used for returning purchase order details in API responses.
* **`OrderStatus`:** An enum defining the possible states of a purchase order (`PENDING`, `DELIVERED`, `CANCELLED`).
* **`InvalidStatusChangeException`:** A custom exception thrown when an invalid status transition is attempted.
* **`PurchaseOrderNotFoundException`:** A custom exception thrown when a requested purchase order is not found.
* **`ServiceException`:** A generic custom exception for service-layer errors.

## Service Dependencies

This service registers itself with the Eureka server for service discovery, as indicated by the `eureka.client.service-url.defaultZone` property in `application.properties`. It does not explicitly call other microservices based on the provided code, but in a complete system, it might interact with:

* **`supplier-service`:** To validate supplier IDs.
* **`product-service`:** To validate product IDs.
* **`stock-service`:** To update stock levels when a purchase order is marked as `DELIVERED`.

## Exception Handling

The service implements global exception handling (though the `GlobalExceptionHandler` file was not explicitly provided, its presence is inferred from the custom exceptions and common Spring Boot patterns). It specifically handles:

* **`PurchaseOrderNotFoundException`:** When a requested purchase order is not found.
* **`InvalidStatusChangeException`:** When an attempt is made to transition a purchase order to an invalid status.
* **`ServiceException`:** For general service-layer errors.
* Validation errors (e.g., from `@Valid` annotations in `PurchaseOrderRequest`).

Appropriate HTTP status codes are returned for different error scenarios.

## Logging

The service uses `logback` for logging application events and errors. The `logback.xml` file (if present) would typically configure logging to both a file (`purchase-service.log`) with daily rolling and a history of 7 days, and to the console. The root log level is set to `INFO`, while the logger specifically for the `com.cts.inventorymanagement.purchase-service` package is set to `DEBUG`, providing more detailed logs for this service.

## Database Configuration

The service is configured to connect to a MySQL database named `purchases` (which will be created if it doesn't exist) running on `localhost:3306`. The database credentials (`spring.datasource.username=root`, `spring.datasource.password=1234`) are specified in `application.properties`. Hibernate is used as the JPA provider, with the dialect set to `org.hibernate.dialect.MySQLDialect` and DDL auto set to `update`. SQL queries are also configured to be shown and formatted in the logs for development purposes.

## Running the Service

To run the `purchase-service` locally:

1.  Ensure you have Java 17 (as specified in `pom.xml`) and Maven installed.
2.  Ensure you have a MySQL instance running on `localhost:3306` with the specified credentials.
3.  Navigate to the root directory of the `purchase-service`.
4.  Build the project using Maven: `mvn clean install`
5.  Run the application using Spring Boot Maven plugin: `mvn spring-boot:run`

The service will be accessible on port `5003` as configured in the `application.properties`. Ensure the `registry` service is also running for full functionality.

## API Documentation

API documentation for this service can be accessed via Swagger UI at `/swagger-ui.html` once the service is running. This is configured by `springdoc.swagger-ui.path` in the `application.properties`.

## Further Considerations

* **Integration with Stock Service:** Implement the logic to update the `stock-service` when a purchase order's status changes to `DELIVERED`. This is a crucial part of an inventory system.
* **Integration with Supplier Service:** If you have a `supplier-service`, integrate it to validate `supplierId` and retrieve supplier details.
* **Security:** Implement robust authentication and authorization mechanisms (e.g., using Spring Security) to protect the API endpoints.
* **Asynchronous Processing:** For long-running operations or integrations, consider using message queues (e.g., Kafka, RabbitMQ) for asynchronous processing.
* **Testing:** Add comprehensive unit, integration, and end-to-end tests to ensure the reliability and correctness of the `purchase-service`.
