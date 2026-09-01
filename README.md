# Employee Management System

## Overview

Employee Management System is a Spring Boot-based backend application designed to manage employee-related operations securely. The project implements authentication and authorization using JWT and role-based access control.

## Features

* User Registration and Login
* JWT Authentication
* Role-Based Authorization
* Secure Password Encryption using BCrypt
* Spring Security Integration
* RESTful APIs
* MySQL Database Integration
* Exception Handling
* Maven Build Management

## Technology Stack

| Category   | Technology           |
| ---------- | -------------------- |
| Language   | Java 21              |
| Framework  | Spring Boot          |
| Security   | Spring Security, JWT |
| Database   | MySQL                |
| Build Tool | Maven                |
| ORM        | Spring Data JPA      |
| Testing    | JUnit                |

## Project Structure

src/main/java
├── DTO
├── Entity
├── Enum
├── Repository
├── Security
└── Service

## API Modules

* Authentication Module

  * User Registration
  * User Login
  * JWT Token Generation

* Authorization Module

  * Role-Based Access Control
  * Permission Management

## Security Features

* JWT Token Authentication
* BCrypt Password Encoding
* Protected API Endpoints
* Role-Based Permissions

## Setup Instructions





### Configure Database

Update the application.properties file with your MySQL credentials.

### Run Application

mvn spring-boot:run

### Build Project

mvn clean install

## Future Enhancements

* Employee CRUD Operations
* Leave Management System
* Attendance Tracking
* Payroll Management
* Department Management
* Dashboard Analytics
* Microservices Architecture

## Author

Ojas Gangwar

## License

This project is created for learning and development purposes.
