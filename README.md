# AcadLink Backend

AcadLink Backend is a robust, scalable, and secure RESTful API server designed for academic collaboration and resource management. Built with modern Java technologies, it emphasizes code quality, maintainability, and extensibility for educational institutions of all sizes.

## Features

- User authentication and authorization
- Course and resource management
- Collaboration tools for students and instructors
- RESTful API endpoints for all major entities
- Role-based access control
- Comprehensive error handling
- <!-- Add your features below -->

## Key Technologies & Architecture

- **Java 17**: Leverages the latest language features for performance and reliability.
- **Spring Boot**: Rapid development of production-grade, stand-alone applications with minimal configuration.
- **Spring Data JPA**: Simplifies database access and ORM with powerful repository abstractions.
- **Maven**: Dependency management and streamlined build lifecycle.
- **Modular Structure**: Clean separation of concerns (API, service, domain, configuration, utility).
- **YAML-based Configuration**: Flexible and environment-specific settings via `application.yml`.

## Quality & Focus

- **Security**: Designed with best practices for authentication and authorization.
- **Maintainability**: Follows SOLID principles, clear package structure, and comprehensive documentation.
- **Extensibility**: Easily adaptable for new features and integrations.
- **Testing**: Includes unit and integration tests for reliability.
- **Performance**: Optimized for high throughput and low latency.

## Getting Started

### Prerequisites

- Java 17 or later
- Maven

### Setup Instructions

1. **Clone the repository**

   ```bash
   git clone <your-repo-url>
   cd backend/acadlink
   ```

2. **Add the `application.yml` file**
   Create `src/main/resources/application.yml` with your environment-specific configuration. Example:

   ```yaml
   server:
     port: 8080

   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/acadlink_db
       username: your_db_user
       password: your_db_password
       driver-class-name: com.mysql.cj.jdbc.Driver
     jpa:
       hibernate:
         ddl-auto: update
       show-sql: true
       database-platform: org.hibernate.dialect.MySQL8Dialect
   ```

   Adjust the database settings as needed for your environment.

## Screenshots

Below are some screenshots demonstrating the API and database schema:

1. **Swagger UI - API List (1)**
   ![Swagger UI - API List 1](docs/api_ss1.png)

2. **Swagger UI - API List (2)**
   ![Swagger UI - API List 2](docs/apis_ss2.png)

3. **API Request Example**
   ![API Request Example](docs/req_ss.png)

4. **API Response Example**
   ![API Response Example](docs/response_ss.png)

5. **Database Schema Diagram**
   ![Database Schema Diagram](docs/schema_ss.png)

## Project Structure

```
backend/
  acadlink/
    src/
      main/
        java/com/majed/acadlink/
        resources/
      test/
        java/com/majed/acadlink/
    pom.xml
```

- `java/com/majed/acadlink/`: Source code (API, service, domain, config, etc.)
- `resources/`: Configuration files (application.yml)
- `test/java/com/majed/acadlink/`: Test cases
- `pom.xml`: Maven build file

## Author

**Majedul Islam**  
Backend Developer | CS Undergrad  
[GitHub](https://github.com/mr-majed7) • [LinkedIn](https://www.linkedin.com/in/majedul-islam-041637220/)  
Interested in backend systems, Java/Spring Boot.
