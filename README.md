# Microservices Architecture - Spring Boot Project

## Overview

This project demonstrates a Microservices-based architecture using Spring Boot, covering:

- Multiple microservices (Accounts, Cards, Loans, Transactions, Notifications)
- Centralized Configuration Management via Config Server
- Service Discovery using Eureka Server
- API Gateway Server for routing, security, and rate limiting
- Asynchronous Communication with RabbitMQ
- Caching using Redis
- Authentication & Authorization using Keycloak (OIDC)
- Containerization with Docker

---

## Tech Stack

| Layer              | Technology                |
|--------------------|---------------------------|
| API Gateway        | Spring Cloud Gateway      |
| Config Management  | Spring Cloud Config Server |
| Service Discovery  | Eureka Server             |
| Messaging Queue    | RabbitMQ                  |
| Security           | Keycloak + OAuth2 (OIDC)  |
| Caching            | Redis                     |
| Database           | MySQL                     |
| Containerization   | Docker + Docker Compose   |
| API Documentation  | Swagger/OpenAPI           |

---

## Microservices Overview

| Microservice  | Port | Description                   |
|---------------|------|-------------------------------|
| **Accounts**      | 8080 | Manages customer account details  |
| **Cards**         | 8090 | Manages customer card details     |
| **Loans**         | 9000 | Manages customer loan details     |
| **Transactions**  | 9020 | Tracks customer transactions      |
| **Notifications** | 9010 | Sends notifications (via RabbitMQ)|

---

## Supporting Services

| Service         | Port | Description                    |
|-----------------|------|--------------------------------|
| **Config Server**   | 8071 | Centralized configuration      |
| **Eureka Server**   | 8070 | Service registry               |
| **Gateway Server**  | 8072 | Entry point for all requests   |
| **RabbitMQ UI**     | 15672| RabbitMQ Management Console   |
| **Keycloak**        | 7080 | Authentication provider (OIDC)|

---

## Running the Project

### Prerequisites

- Java 17
- Maven
- Docker + Docker Compose
- MySQL (port 3307)

### Start with Docker Compose

```bash
docker-compose up --build
