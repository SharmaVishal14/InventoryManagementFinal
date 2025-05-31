# 🏢 Inventory Management System (IMS)

## 📄 Overview

The Inventory Management System is a comprehensive microservices-based application designed to efficiently manage product stock, track inventory levels, handle customer orders, manage supplier relationships, and process purchase orders. The system is built with a distributed architecture using Spring Boot microservices, Spring Cloud for service discovery (Eureka), and an API Gateway for unified access and cross-cutting concerns like authentication.

## 🏛️ Architecture Overview

The system follows a microservices architectural style, where each core business capability is encapsulated within an independent service. These services communicate with each other primarily via REST APIs and are orchestrated through a Service Registry and an API Gateway.

* **Microservices:** Independent, loosely coupled services handling specific business domains (Product, Stock, Order, Purchase, Supplier, Auth).

* **Service Registry (Eureka Server):** A central server for service registration and discovery, allowing services to find each other dynamically.

* **API Gateway (Spring Cloud Gateway):** The single entry point for all client requests, responsible for routing, authentication, and logging.

* **Relational Databases (MySQL):** Each service typically has its own dedicated database for data persistence, ensuring data independence.

## 🚀 Microservices Breakdown

Here's a brief overview of each microservice in the system. For more detailed information, please refer to their respective `README.md` files located in their individual project directories.

### ⚙️ Service Registry (Eureka Server)

* **Purpose:** The central hub for service registration and discovery. All other microservices register themselves here, allowing them to be discovered by the API Gateway and other services.

* **Key Functionality:** Service registration, service discovery, health monitoring of registered instances.

* **Further Details:** Refer to the `registry/README.md` for more information.

### 🔑 Auth Service

* **Purpose:** Handles user authentication and authorization. It provides functionality for user login and generates JWT (JSON Web Tokens) for secure access to other services.

* **Key Functionality:** User authentication (login), JWT generation, JWT validation, custom user details loading.

* **Further Details:** Refer to the `auth-service/README.md` for more information.

### 📦 Product Service

* **Purpose:** Manages all product-related information, including adding, updating, retrieving, and deleting product details. It integrates with the `stock-service` to manage inventory levels.

* **Key Functionality:** CRUD operations for products, updating product stock levels (delegating to `stock-service`).

* **Further Details:** Refer to the `product-service/README.md` for more information.

### 📊 Stock Service

* **Purpose:** Manages the inventory levels of products. It tracks quantities, updates stock based on orders/shipments, and manages reorder levels.

* **Key Functionality:** Initializing stock, retrieving stock, updating stock levels (increment/decrement), updating reorder levels, checking stock sufficiency.

* **Further Details:** Refer to the `stock-service/README.md` for more information.

### 🛒 Order Service

* **Purpose:** Responsible for managing customer orders. It handles the creation of new orders, retrieves order details, and interacts with `product-service` and `stock-service` for product availability and inventory updates.

* **Key Functionality:** Creating new orders, retrieving orders by customer or product, updating order status.

* **Further Details:** Refer to the `order-service/README.md` for more information.

### 🧾 Purchase Service

* **Purpose:** Manages the procurement of products from suppliers. It handles the creation, tracking, and management of purchase orders.

* **Key Functionality:** Creating purchase orders, retrieving purchase orders (by ID, supplier, product, status, date range), updating purchase order status, deleting purchase orders.

* **Further Details:** Refer to the `purchase-service/README.md` for more information.

### 🧑‍🤝‍🧑 Supplier Service

* **Purpose:** Manages information about the suppliers from whom the business procures products.

* **Key Functionality:** Adding new suppliers, retrieving supplier details, updating supplier contact information, updating supplier status.

* **Further Details:** Refer to the `supplier-service/README.md` for more information.

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

* **Java Development Kit (JDK) 17 or higher**

* **Apache Maven 3.6.0 or higher**

* **MySQL Database Server** (running on `localhost:3306` with `root` user and `root` password, or adjust `application.properties` files accordingly).

* **Git** (for cloning the repository)

## ▶️ Running the Application

To run the entire Inventory Management System, you need to start each microservice in a specific order. Ensure your MySQL server is running before starting any services.

**Database Setup:**
Each service will attempt to create its database (`products`, `stocks`, `orders`, `purchases`, `suppliers`, `auth`) if it doesn't exist, as configured by `createDatabaseIfNotExist=true` in their respective `application.properties` files.

### Step-by-Step Startup Order:

1.  **Start the Service Registry (Eureka Server):**

    ```bash
    cd registry
    mvn clean install
    mvn spring-boot:run
    ```

    Wait for the Eureka Server to start. You can verify it by opening `http://localhost:8761` in your browser.

2.  **Start the Auth Service:**

    ```bash
    cd auth-service
    mvn clean install
    mvn spring-boot:run
    ```

    The Auth Service will run on port `9898`.

3.  **Start Core Business Services (Product, Stock, Order, Purchase, Supplier):**

    Open **separate terminal windows** for each of these services.

    For each service (e.g., `product-service`, `stock-service`, `order-service`, `purchase-service`, `supplier-service`):

    ```bash
    cd <service-name>
    mvn clean install
    mvn spring-boot:run
    ```

    **Ports:**

    * `product-service`: `5000`
    * `stock-service`: `5004`
    * `order-service`: `5001`
    * `purchase-service`: `5003`
    * `supplier-service`: `5005`

    Wait for all services to register with the Eureka Server. You can check the Eureka dashboard (`http://localhost:8761`) to see if they are registered.

4.  **Start the API Gateway:**

    ```bash
    cd api-gateway
    mvn clean install
    mvn spring-boot:run
    ```

    The API Gateway will run on port `9090`.

### 🌐 Accessing the Application

You have two primary ways to interact with the application:

#### Method 1: Direct Access via Individual Service Swagger UIs

This method allows you to test each microservice independently.

1.  Ensure all services are running as described in the "Step-by-Step Startup Order" above.

2.  Access the Swagger UI for each service directly at their respective ports:

    * **Product Service:** `http://localhost:5000/swagger-ui.html`
    * **Stock Service:** `http://localhost:5004/swagger-ui.html`
    * **Order Service:** `http://localhost:5001/swagger-ui.html`
    * **Purchase Service:** `http://localhost:5003/swagger-ui.html`
    * **Supplier Service:** `http://localhost:5005/swagger-ui.html`
    * **Auth Service:** `http://localhost:9898/swagger-ui.html`

    *Note: When using this method, you will need to handle authentication (e.g., obtaining a JWT from the Auth Service) and include it in the headers for secured endpoints manually for each service.*

#### Method 2: Unified Access via API Gateway

This is the recommended way to interact with the system as a client, as the API Gateway handles routing and authentication.

1.  Ensure all services (Registry, Auth, Product, Stock, Order, Purchase, Supplier, and API Gateway) are running as described in the "Step-by-Step Startup Order" above.

2.  **Authenticate via Auth Service:**

    * First, you need to obtain a JWT token from the Auth Service. You can do this by sending a `POST` request to `http://localhost:9090/auth/token` (via the Gateway) with your user credentials.

    * Example (using a tool like Postman or curl):

        ```bash
        curl -X POST "http://localhost:9090/auth/token" \
             -H "Content-Type: application/json" \
             -d '{
                   "email": "your_email@example.com",
                   "password": "your_password"
                 }'
        ```

        (Replace `your_email@example.com` and `your_password` with actual user credentials you've registered with the Auth Service).

    * The response will contain your JWT token. Copy this token.

3.  **Access Services via API Gateway:**

    * All subsequent requests to the business services should be made through the API Gateway's port (`9090`).

    * You **must** include the obtained JWT token in the `Authorization` header of your requests in the format `Bearer <YOUR_JWT_TOKEN>`.

    * **Example API Gateway routes:**

        * **Product Service:** `http://localhost:9090/products/...`

        * **Stock Service:** `http://localhost:9090/stocks/...`

        * **Order Service:** `http://localhost:9090/orders/...`

        * **Purchase Service:** `http://localhost:9090/purchase-orders/...`

        * **Supplier Service:** `http://localhost:9090/suppliers/...`

    *Example (using curl to get all products via Gateway with JWT):*

    ```bash
    curl -X GET "http://localhost:9090/products" \
         -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
    ```

    (Replace `<YOUR_JWT_TOKEN>` with the actual token you obtained)

## 🤔 Further Considerations

* **Security:** Implement robust authentication and authorization mechanisms (e.g., using Spring Security) for all services and the API Gateway.

* **Scalability & High Availability:** For production, consider deploying multiple instances of each service and the Eureka Server for high availability.

* **Asynchronous Communication:** For complex workflows or decoupling, consider integrating message brokers (e.g., Kafka, RabbitMQ).

* **Monitoring & Tracing:** Utilize tools like Spring Boot Actuator, Micrometer, and distributed tracing (Spring Cloud Sleuth/Zipkin) for comprehensive monitoring.

* **Centralized Configuration:** Consider Spring Cloud Config Server for centralized management of application configurations.

* **Containerization & Orchestration:** Package services into Docker containers and deploy using Kubernetes or Docker Compose for easier management and scaling.

* **Testing:** Implement comprehensive unit, integration, and end-to-end tests across all microservices.
