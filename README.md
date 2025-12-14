# Secure Task App

A small **secure task management backend**, focusing on **clean architecture, security, and DevOps practices**. This repository is intended to be easily understandable by **recruiters and developers**.

---

## Overview

This project demonstrates:

* Authentication & authorization using JWT
* Task management with protected endpoints
* Containerized local development
* CI/CD basics with Jenkins
* Centralized logging and observability

The frontend is intentionally minimal / optional — the core focus is the **backend architecture and infrastructure**.

---

## Tech Stack

**Backend**

* Java 21
* Spring Boot 3
* Spring Security (JWT)
* Maven (multi-module)

**Databases**

* MySQL (Auth DB & Task DB)

**Infrastructure & DevOps**

* Docker & Docker Compose
* Jenkins (CI pipeline)
* Elasticsearch
* Logstash

**API & Docs**

* Swagger / OpenAPI

---

## Project Structure (simplified)

```
backend/
  ├── auth-service
  ├── task-service
  └── common

docker-compose.yml
Jenkinsfile
run.sh / run.bat
```

---

## Getting Started

### Prerequisites

* Docker + Docker Compose
* Java 21 (only if running services outside Docker)

---

### Quick Start (Recommended)

From the project root:

```bash
./run.sh
```

Follow instructions in terminal

This will start:

* Jenkins
* Elasticsearch
* MySQL databases
* Backend services


---

## Service URLs

Once everything is running:

* **Jenkins**: [http://localhost:9090](http://localhost:9090)
* **Auth Service API**: [http://localhost:8080](http://localhost:8080)
* **Task Service API**: [http://localhost:8081](http://localhost:8081)
* **Swagger (Auth)**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Swagger (Task)**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
* **Elasticsearch**: [http://localhost:9200](http://localhost:9200)

---

## What Is Implemented

* User registration & login
* JWT-based authentication
* Role-based access control
* CRUD operations for tasks
* Dockerized local environment
* Jenkins pipeline for build & startup

---

## In Progress / Planned

* Simple frontend demo (for API visualization)
* Logstash + Elasticsearch (currently networking problem)
* Metrics with Kibana
* Better logging principles
* Image caching for faster deployment

---

## Notes for Recruiters and Devs

This project is **not meant to be a finished product**, but a **technical showcase** demonstrating:

* Backend design
* Security fundamentals
* Infrastructure awareness
* Ability to work with real-world tooling (Docker, CI, logging)

---

## Author

Jean-Baptiste Maene

