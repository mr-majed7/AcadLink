# AcadLink 📚

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://swagger.io/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![SonarCloud](https://img.shields.io/badge/SonarCloud-F3702A?style=for-the-badge&logo=sonarcloud&logoColor=white)](https://sonarcloud.io/)

<div align="center">
  <h2>Academic Collaboration Made Simple</h2>
  
  <p align="center">
    <b>AcadLink</b> is a modern academic collaboration platform that enables students to share and manage educational materials while connecting with peers.
    <br />
    <a href="#-getting-started"><strong>Quick Start »</strong></a>
    <br />
    <br />
    <a href="#-key-features">Features</a>
    ·
    <a href="#-database-design">Database Design</a>
    ·
    <a href="#-api-documentation">API Docs</a>
  </p>
</div>

### SonarCloud Analysis

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mr-majed7_AcadLink&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=mr-majed7_AcadLink)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=mr-majed7_AcadLink&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=mr-majed7_AcadLink)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=mr-majed7_AcadLink&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=mr-majed7_AcadLink)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=mr-majed7_AcadLink&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=mr-majed7_AcadLink)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=mr-majed7_AcadLink&metric=bugs)](https://sonarcloud.io/summary/new_code?id=mr-majed7_AcadLink)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=mr-majed7_AcadLink&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=mr-majed7_AcadLink)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=mr-majed7_AcadLink&metric=coverage)](https://sonarcloud.io/summary/new_code?id=mr-majed7_AcadLink)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=mr-majed7_AcadLink&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=mr-majed7_AcadLink)

## 📋 Table of Contents

- [Key Features](#-key-features)
- [Technical Stack](#-technical-stack)
- [Security Features](#-security-features)
- [Database Design](#-database-design)
- [API Documentation](#-api-documentation)
- [Getting Started](#-getting-started)
- [Testing & Quality](#-testing--quality)
- [Project Structure](#-project-structure)
- [Author](#-author)

## 🚀 Key Features

- Secure user authentication and profile management
- Material upload and organization with privacy controls
- Folder-based content management
- Peer collaboration and material sharing
- Advanced search capabilities for materials and users

## 🛠 Technical Stack

### Backend

- **Java Version**: 21
- **Framework**: Spring Boot 3.4.1
- **Security**: Spring Security with JWT
- **Database**: MySQL 8
- **Documentation**: Swagger/OpenAPI
- **Testing**: JUnit 5, Mockito
- **Code Quality**: SonarQube, JaCoCo

### Key Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Mail
- JWT for authentication
- Lombok for code reduction
- Vavr for functional programming
- SpringDoc for API documentation

## 🔒 Security Features

- JWT-based authentication
- Stateless security model
- CSRF protection (disabled for JWT API)
- Password encryption using BCrypt
- Secure file storage
- Privacy controls for materials and folders

## 📊 Database Design

The database schema is designed to efficiently manage users, materials, folders, and peer relationships. Here's a visual representation of our database structure:

![Database Schema](docs/schema_ss.png)

### Key Entities

- **Users**: Stores user profiles and authentication details
- **Folders**: Manages folder hierarchy and privacy settings
- **Materials**: Handles material metadata and storage paths
- **Peer Relationships**: Tracks peer connections and request status

## 📚 API Documentation

The API documentation is available through Swagger UI at the root path (`/`). The API is organized into the following sections:

1. Public Endpoints (Sign Up, Sign In)
2. Profile Management
3. Folder Management
4. Materials Management
5. Find Materials
6. Peer Management

## 🖥️ API Interface Preview

Here are some screenshots of our API interface and documentation:

1. **Swagger UI - API List (1)**
   ![Swagger UI - API List 1](docs/api_ss1.png)

2. **Swagger UI - API List (2)**
   ![Swagger UI - API List 2](docs/apis_ss2.png)

3. **API Request Example**
   ![API Request Example](docs/req_ss.png)

4. **API Response Example**
   ![API Response Example](docs/response_ss.png)

## 🚀 Getting Started

### Prerequisites

- Java 21 or later
- Maven
- MySQL 8

### Setup Instructions

1. **Clone the repository**

   ```bash
   git clone <your-repo-url>
   cd backend/acadlink
   ```

2. **Configure the application**
   - Copy `example_application.yml` from the root directory to `src/main/resources/application.yml`
   - Update the configuration values in `application.yml` with your environment-specific settings
   - Make sure to set proper values for:
     - Database credentials
     - Email settings
     - JWT secret
     - Storage path
     - Sonar token (if using SonarQube)

3. **Build and Run**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## 📁 Project Structure

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

## 🧪 Testing & Quality

- Comprehensive unit testing with JUnit 5
- Mockito for mocking dependencies
- SonarQube integration for code quality
- JaCoCo for code coverage reporting
- Automated test execution with Maven

## 👨‍💻 Author

**Majedul Islam**  
Backend Developer | CS Undergrad  
[GitHub](https://github.com/mr-majed7) • [LinkedIn](https://www.linkedin.com/in/majedul-islam-041637220/)  
Interested in backend systems, Java/Spring Boot.

## Code Quality

This project uses SonarCloud for continuous code quality monitoring. The analysis is automatically performed on every push to the main branch and on pull requests.

### Quality Metrics

- **Code Quality**: Monitored through SonarCloud's quality gates
- **Code Coverage**: Tracked for both backend and frontend code
- **Code Smells**: Automatically detected and reported
- **Security Vulnerabilities**: Continuously scanned and reported

You can view the detailed analysis and metrics at: [SonarCloud Dashboard](https://sonarcloud.io/summary/overall?id=mr-majed7_AcadLink&branch=main)
