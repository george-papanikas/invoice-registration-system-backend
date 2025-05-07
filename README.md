# Invoice Registration System - Backend (Spring Boot)
This is the backend API for the **Invoice Registration System** full-stack project. It provides RESTful endpoints for managing invoices and customers, secured with JWT-based authentication. The API is fully documented with Swagger/OpenAPI and includes thorough testing using JUnit, MockMvc, and H2.

## 🚀 Tech Stack
- Java 17
- Spring Boot
- Spring Security (JWT-based)
- Spring Data JPA (Hibernate)
- MySQL
- Gradle
- Swagger/OpenAPI (Springdoc)
- SLF4J & Logback (logging)
- JUnit 5
- MockMvc
- H2 (in-memory testing database)

## 📦 Features
- JWT-based authentication & authorization
- Secure user registration and login
- CRUD operations for invoices and customers
- Relational database with Hibernate ORM
- CORS setup for frontend integration (React)
- RESTful API architecture
- Full Swagger documentation
- Unit tests with JUnit, H2, and MockMvc

## 📃 Logging
This project uses SLF4J with Logback for structured, console-based application logging. Logs help monitor API calls, debug errors, and trace execution flow.

✅ The logging is configured in src/main/resources/logback.xml

## 📖 API Documentation (Swagger)
Once the backend is running locally, access Swagger UI at:
👉 http://localhost:8080/swagger-ui/index.html

💡 Use this to explore and test all endpoints interactively.

## 📬 API Overview
### 1. 🔐 Authentication Endpoints
| Method | Endpoint            | Description           |
|--------|---------------------|-----------------------|
| POST   | /api/auth/register  | Register a new user   |
| POST   | /api/auth/login     | Authenticate a user   |		
### 2. 🧾 Invoices
| Method | Endpoint              | Description           |
|--------|-----------------------|-----------------------|
| GET    | /api/invoices         | Get all invoices      |
| GET    | /api/invoices/{id}    | Get invoice by ID     |
| POST   | /api/invoices         | Create a new invoice  |
| PUT    | /api/invoices/{id}    | Update an invoice     |
| DELETE | /api/invoices/{id}    | Delete an invoice     |
### 3. 👤 Customers
| Method | Endpoint               | Description             |
|--------|------------------------|-------------------------|
| GET    | /api/customers         | Get all customers        |
| GET    | /api/customers/{id}    | Get customer by ID       |
| POST   | /api/customers         | Create a new customer    |
| PUT    | /api/customers/{id}    | Update a customer        |
| DELETE | /api/customers/{id}    | Delete a customer        |

## 🔧 How to Run Locally
### 1. 🧬 Clone the Repository
```bash
git clone https://github.com/georgepapanikas/invoice-registration-system-backend.git
```
### 2. 🗄️ Main Database Configuration
Create a file named application-dev.properties to configure your development database connection:
```properties
# ------- MySQL datasource -------
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/mysql_database_name
spring.datasource.username=mysql_username
spring.datasource.password=mysql_password

# ------- JPA / Hibernate --------------
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.globally_quoted_identifiers=true

# ------- JWT --------------
app.jwt-secret=jwt_secret_key
app.jwt-expiration-milliseconds=your_jwt_expiration_time
```
🔐 Replace `mysql_database_name`, `mysql_username`, and `mysql_password` with your actual MySQL configuration values. Likewise, replace `jwt_secret_key` with your secret signing key and `your_jwt_expiration_time` with the desired expiration duration in milliseconds (e.g., `3600000` for 1 hour).
### 3.❗User Roles Setup
The `roles` table in the database must be manually populated with the following roles:
- `ROLE_USER` — This is the default role assigned to every newly registered user.  
- `ROLE_ADMIN` — This role is manually granted to specific users who require administrative privileges.  

💡 Make sure both roles exist in the `roles` table before any user registration takes place to avoid role assignment issues. 

🔁 Alternatively, you can insert them by adding the following SQL statements to a data.sql file located in src/main/resources:
```sql
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
```
📌 Spring Boot will automatically execute this file on startup if spring.sql.init.mode=always and spring.jpa.defer-datasource-initialization=true are set in your application-dev.properties.
### 4. ▶️ Run the Application
To start the Spring Boot application using Gradle, run the following command in your project root directory:
```bash
./gradlew bootRun
```
💡 Make sure your database is running and your application-dev.properties is properly configured before starting the application.

## 🧪 Testing
This project includes thorough unit testing using:
- JUnit 5
- H2 in-memory database (via application-test.properties)
- MockMvc for controller testing
### 1.🔍 Tested Layers
- Repository
- Service
- Controller
### 2.🧾 Test Database Configuration
Create a file named application-test.properties to configure the test database environment using an in-memory H2 database:
```properties
# ------- H2 in-memory datasource -------
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
# ------- JPA / Hibernate --------------
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# --- ensure data.sql runs only AFTER Hibernate DDL ---
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```
🧪 This configuration ensures a fresh schema for each test run and allows automatic loading of test data via data.sql.
### 3. ▶️ Run Tests
To execute the unit and integration tests, run the following command from the project root:
```bash
./gradlew test
```
🧪 Ensure your application-test.properties is correctly set up before running the tests.

## 🗂️ Project Structure
```bash
src/
├── main/
│   ├── java/com/github/georgepapanikas/invoiceregistrationsystem
│   │   ├── configuration/
│   │   ├── dto/
│   │   ├── mapper/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── rest/
│   │   ├── service/
│   │   └── InvoiceRegistrationSystemApplication.java
│   └── resources/
│       └── application-dev.properties
│       └── logback.xml
├── test/
│   └── java/com/github/georgepapanikas/invoiceregistrationsystem
│       ├── repository/
│       ├── rest/
│       └── service/
│   └── resources/
│       └── application-test.properties
│       └── data.sql
```

## 👤 Author
**George Papanikas**  
Junior Software Developer  
[LinkedIn](https://linkedin.com/in/georgepapanikas) | [GitHub](https://github.com/george-papanikas)

## 📄 License
This project is licensed under the MIT License — see the LICENSE file for details.

## 🌐 Related Repositories
🔗 [Frontend (React)](https://github.com/george-papanikas/invoice-registration-system-frontend)
