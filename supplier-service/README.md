# Supplier Service

## Overview

The `supplier-service` is a core microservice within the Inventory Management System, responsible for managing information about the suppliers from whom the business procures products. It handles the creation, retrieval, updating, and management of supplier details and their status.

## Technologies Used

* **Java:** Programming language used for the service implementation.
* **Spring Boot:** Framework for building the microservice.
* **Spring Web:** For building RESTful APIs.
* **Spring Data JPA:** For database interaction through the `SupplierRepository`.
* **Lombok:** For reducing boilerplate code in Java classes.
* **MySQL:** Relational database used for persisting supplier information (configured in `application.properties`).
* **Maven:** Build automation tool (configured in `pom.xml`, if provided).
* **Logback:** Logging framework (standard Spring Boot setup, often configured via `logback.xml`).
* **SpringDoc OpenAPI:** For generating and serving Swagger API documentation.
* **Eureka Client:** For service registration and discovery within the microservices architecture.
* **Spring Boot Starter Validation:** For declarative validation of request bodies.
* **Spring Boot Starter Actuator:** For monitoring and managing the application.

## Functionality

The `supplier-service` provides the following functionalities:

* **Adding a new supplier:** Allows the creation of new supplier records with details such as name, contact information (likely using the `ContactInfo` entity), and status.
* **Retrieving supplier details:** Supports fetching supplier information by ID and potentially retrieving all suppliers or suppliers based on their status.
* **Updating supplier information:** Enables modification of existing supplier details, including contact information.
* **Updating supplier status:** Allows changing the operational status of a supplier (e.g., ACTIVE, INACTIVE).

## API Endpoints

The service exposes the following REST API endpoints:

* `POST /suppliers`: Creates a new supplier. Accepts a `SupplierRequest` in the request body. Returns a `SupplierResponse`.
* `GET /suppliers/{id}`: Retrieves details of a specific supplier by their ID. Returns a `SupplierDetailsResponse`.
* `GET /suppliers`: Retrieves a list of all suppliers. Returns a list of `SupplierResponse`.
* `PUT /suppliers/{id}`: Updates the general information of a supplier. Accepts a `SupplierRequest` in the request body. Returns a `SupplierResponse`.
* `PATCH /suppliers/{id}/contact`: Updates the contact information of a supplier. Accepts a `ContactUpdateRequest` in the request body. Returns a `SupplierResponse`.
* `PATCH /suppliers/{id}/status`: Updates the status of a supplier. Accepts a `SupplierStatusUpdateRequest` in the request body. Returns a `SupplierResponse`.

## Data Models

The service utilizes the following data models:

* **`Supplier`:** The JPA entity representing a supplier in the database, including `id`, `name`, `contactInfo` (an embedded or related entity), and `status`.
* **`ContactInfo`:** An embedded or related entity containing contact details for the supplier (e.g., email, phone number, address).
* **`SupplierRequest`:** A DTO used for creating new suppliers and updating general supplier information. It likely includes validation annotations.
* **`SupplierResponse`:** A DTO used for returning basic supplier information in API responses.
* **`SupplierDetailsResponse`:** A DTO used for returning detailed information about a specific supplier, potentially including contact details.
* **`SupplierStatus`:** An enum defining the possible operational statuses of a supplier (e.g., `ACTIVE`, `INACTIVE`).
* **`SupplierStatusUpdateRequest`:** A DTO used for updating the status of a supplier.
* **`ContactUpdateRequest`:** A DTO used for updating the contact information of a supplier.
* **`SupplierNotFoundException`:** A custom exception thrown when a requested supplier is not found.
* **`InvalidStatusChangeException`:** A custom exception thrown when an attempt is made to change the supplier's status to an invalid state.
* **`ErrorResponse`:** A structured object for returning error information in API responses.

## Service Dependencies

This service registers itself with the Eureka server for service discovery (likely configured in `application.properties`). Based on the provided files, it does not have explicit dependencies on other microservices. However, in a complete system, the `purchase-service` might interact with this service to retrieve supplier information.

## Exception Handling

The service implements global exception handling (likely using `GlobalExceptionHandler`). It specifically handles:

* **`SupplierNotFoundException`:** When a requested supplier is not found.
* **`InvalidStatusChangeException`:** When an attempt is made to change the supplier's status to an invalid state.
* Validation errors (e.g., from `@Valid` annotations in request DTOs).

Appropriate HTTP status codes are returned for different error scenarios.

## Logging

The service uses `logback` for logging application events and errors. The `logback.xml` file (if present) would typically configure logging to both a file (`supplier-service.log`) with daily rolling and a history of 7 days, and to the console. The root log level is set to `INFO`, while the logger specifically for the `com.cts.inventorymanagement.supplier-service` package might be set to `DEBUG` for more detailed logging.

## Database Configuration

The service is likely configured to connect to a MySQL database named `suppliers` (which will be created if it doesn't exist) running on `localhost:3306`. The database credentials (`spring.datasource.username` and `spring.datasource.password`) are likely set in the `application.properties`. Hibernate is used as the JPA provider for database interactions.

## Running the Service

To run the `supplier-service` locally:

1.  Ensure you have Java and Maven installed.
2.  Ensure you have a MySQL instance running on `localhost:3306` with the necessary database configured.
3.  Navigate to the root directory of the `supplier-service`.
4.  Build the project using Maven: `mvn clean install`
5.  Run the application using Spring Boot Maven plugin: `mvn spring-boot:run`

The service will be accessible on a specific port (likely different from other services, defined in its `application.properties`). Ensure the `registry` service is also running for full functionality.

## API Documentation

API documentation for this service can be accessed via Swagger UI at `/swagger-ui.html` once the service is running (likely configured in `application.properties`).

## Further Considerations

* **Relationship with Products:** Consider how the service might be extended to manage the products supplied by each supplier.
* **Auditing:** Implement auditing to track changes made to supplier information.
* **Security:** Implement robust authentication and authorization mechanisms to protect the API endpoints.
* **Testing:** Add comprehensive unit and integration tests to ensure the reliability and correctness of the `supplier-service`.
