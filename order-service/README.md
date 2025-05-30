# Order Service

## Overview

The `order-service` is a core microservice within the Inventory Management System responsible for managing customer orders. It handles the creation of new orders, retrieving order details, and interacts with other services (specifically `product-service` and `stock-service`) to ensure product availability and update inventory levels upon order placement.

## Technologies Used

* **Java:** Programming language used for the service implementation.
* **Spring Boot:** Framework for building the microservice.
* **Spring Web:** For building RESTful APIs.
* **Spring Data JPA:** For database interaction through the `OrderRepository` and `OrderItemRepository`.
* **Spring Cloud OpenFeign:** For declarative HTTP client to communicate with the `product-service` (via `ProductClient`) and `stock-service` (via `StockClient`).
* **Lombok:** For reducing boilerplate code in Java classes (implicitly used through annotations).
* **MySQL:** Relational database used for persisting order information (configured in `application.properties`).
* **Maven:** Build automation tool (based on standard Spring Boot project structure).
* **Logback:** Logging framework (configuration likely similar to other services).
* **SpringDoc OpenAPI:** For generating and serving Swagger API documentation (likely configured).
* **Eureka Client:** For service registration and discovery within the microservices architecture (likely configured).

## Functionality

The `order-service` provides the following functionalities:

* **Creating a new order:** Accepts order details (including customer information and order items) and persists the order in the database. This process involves:
    * Retrieving product details from the `product-service`.
    * Checking stock availability with the `stock-service`.
    * Updating stock levels in the `stock-service` upon successful order creation.
* **Retrieving order details:** Allows fetching order information based on a unique order ID.

## API Endpoints

The service exposes the following REST API endpoints:

* `POST /api/orders`: Creates a new order. Accepts an `OrderInputDto` in the request body and returns an `OrderItemOutputDto`.
* `GET /api/orders/{orderId}`: Retrieves the details of a specific order by its ID. Returns an `OrderOutputDto`.

## Data Models

The service utilizes the following data models:

* **`Order`:** Represents the order entity stored in the database, including customer details and a list of order items.
* **`OrderItem`:** Represents an individual item within an order, linking a product to the ordered quantity.
* **`OrderInputDto`:** A Data Transfer Object used for receiving order creation requests, containing customer information and a list of `OrderItemInputDto`.
* **`OrderItemInputDto`:** A Data Transfer Object representing an item to be ordered, including the `productId` and `quantity`.
* **`OrderOutputDto`:** A Data Transfer Object used for returning detailed order information in API responses, including customer details and a list of `OrderItemOutputDto`.
* **`OrderItemOutputDto`:** A Data Transfer Object representing an individual item in the order response, including product details and the ordered quantity.
* **`ProductDto`:** A Data Transfer Object representing product information retrieved from the `product-service`.
* **`StockDto`:** A Data Transfer Object representing stock information retrieved from the `stock-service`.
* **`StockUpdateRequest`:** Represents the request body for updating stock levels sent to the `stock-service`.
* **`ErrorResponse`:** A structured object for returning error information in API responses.

## Service Dependencies

This service depends on the following other microservices:

* **`product-service`:** The `order-service` communicates with the `product-service` to retrieve product details (name, price, etc.) for the items being ordered. It uses a Feign Client (`ProductClient`) for this.
* **`stock-service`:** The `order-service` communicates with the `stock-service` to check the availability of products being ordered and to update the stock levels after a successful order creation. It uses a Feign Client (`StockClient`) for this.
* **`registry` (Eureka Server):** The service registers itself with the Eureka server for service discovery (likely configured in `application.properties` with `spring.application.name=order-service`).

## Exception Handling

The service implements global exception handling using `GlobalExceptionHandler`. It specifically handles:

* **`ProductNotFoundException`:** When a requested product (part of the order) does not exist in the `product-service`.
* **`InsufficientStockException`:** When there is not enough stock available for a product being ordered, as determined by the `stock-service`.
* **Other potential exceptions:** The `GlobalExceptionHandler` likely handles other general exceptions as well, returning structured `ErrorResponse` objects with appropriate HTTP status codes.

## Logging

The service uses `logback` for logging application events and errors (configuration likely similar to other services, potentially logging to a file and the console).

## Database Configuration

The service is likely configured to connect to a MySQL database named `orders` running on `localhost:3306`. The database credentials (`spring.datasource.username` and `spring.datasource.password`) are likely set in the `application.properties`. Hibernate is used as the JPA provider for database interactions.

## Running the Service

To run the `order-service` locally:

1.  Ensure you have Java and Maven installed.
2.  Ensure you have a MySQL instance running on `localhost:3306` with the `orders` database configured.
3.  Navigate to the root directory of the `order-service`.
4.  Build the project using Maven: `mvn clean install`
5.  Run the application using Spring Boot Maven plugin: `mvn spring-boot:run`

The service will be accessible on port `5001` (as assumed based on previous configuration). Ensure the `registry`, `product-service`, and `stock-service` are also running for full functionality.

## API Documentation

API documentation for this service can be accessed via Swagger UI at `/swagger-ui.html` once the service is running (likely configured in `application.properties`).

## Further Considerations

* **Order Status Management:** Consider adding functionality to track and update the status of orders (e.g., pending, processing, shipped, delivered).
* **Payment Processing:** Integration with a payment gateway would be a crucial aspect of a real-world order management system.
* **Asynchronous Communication:** For order placement and stock updates, consider using asynchronous communication (e.g., Kafka, RabbitMQ) to improve resilience and decouple services.
* **Transaction Management:** Ensure proper transaction management across multiple service calls (e.g., when creating an order and updating stock).
* **Input Validation:** Implement robust input validation for the order creation request.
* **Testing:** Add unit and integration tests to ensure the reliability and correctness of the `order-service`.
