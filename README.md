# 🧺 LaundryWeb — Student Laundry Reservation System

A full-stack web application that allows students living in a dormitory to view washing machine availability and book time slots online — no more waiting in front of the laundry room.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Known Limitations](#known-limitations)

---

## Overview

LaundryWeb is a reservation platform for student dormitories. Students can:

- Register and log in to their account
- View all washing machines and their current status
- See available time slots for a machine on a given day
- Book a slot with a chosen wash type and duration
- The system automatically blocks surrounding slots based on wash duration + a 15-minute buffer

---

## Tech Stack

| Layer     | Technology                        |
|-----------|-----------------------------------|
| Frontend  | React 18, plain CSS               |
| Backend   | Spring Boot 3.5, Java 21          |
| Database  | MySQL (production), H2 (testing)  |
| ORM       | Hibernate / Spring Data JPA       |
| API Docs  | SpringDoc OpenAPI (Swagger UI)    |
| Testing   | JUnit 5, Mockito, MockMvc         |

---

## Architecture

```
┌─────────────────────┐
│     React Frontend  │  http://localhost:3000
│  App.js             │
│  WasherCard.js      │
│  LoginModal.js      │
│  SignupModal.js      │
└────────┬────────────┘
         │ HTTP (fetch)
         ▼
┌─────────────────────┐
│   Spring Boot API   │  http://localhost:8080
│                     │
│  /students          │
│  /washmachines      │
│  /slots             │
│  /reservations      │
└────────┬────────────┘
         │ JPA / Hibernate
         ▼
┌─────────────────────┐
│      MySQL DB       │
│                     │
│  Student            │
│  Washmachine        │
│  Reservation        │
└─────────────────────┘
```

---

## Project Structure

### Backend (`src/main/java/com/start/waschmachine/`)

```
├── Reservation/
│   ├── Reservation.java              # Entity
│   ├── ReservationController.java    # POST /reservations/create, GET /reservations/all
│   ├── ReservationRepository.java    # isSlotTaken(), findByMachineIdAndDate()
│   ├── ReservationRequest.java       # DTO for incoming requests
│   ├── ReservationService.java       # Business logic
│   └── SlotController.java           # GET /slots/available
│
├── Student/
│   ├── Student.java                  # Entity
│   ├── StudentController.java        # POST /students/register, POST /students/login
│   ├── StudentRepository.java
│   ├── StudentService.java
│   └── LoginRequest.java             # DTO
│
├── Washmachine/
│   ├── Washmachine.java              # Entity
│   ├── WashmachineController.java    # GET /washmachines/all
│   ├── WashmachineRepository.java
│   └── WashmachineService.java
│
└── WaschmachineApplication.java      # Entry point
```

### Frontend (`src/`)

```
├── App.js              # Main component, fetches machines, handles auth state
├── App.css             # Global styles
├── WasherCard.js       # Displays a single washing machine card
├── LoginModal.js       # Login form modal
├── SignupModal.js       # Registration form modal
├── index.js            # React entry point
└── index.html          # HTML template
```

---

## Getting Started

### Prerequisites

- Java 21
- Node.js 18+
- MySQL 8+
- Maven

### 1. Database Setup

Create a MySQL database and update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/laundry_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

### 2. Run the Backend

```bash
cd waschmachine
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Run the Frontend

```bash
cd frontend
npm install
npm start
```

The app will open at `http://localhost:3000`

---

## API Reference

### Students

#### Register a new student
```
POST /students/register
Content-Type: application/json

{
  "vorname": "John",
  "nachname": "Doe",
  "email": "john@dorm.com",
  "password": "secret123"
}
```
**Response:** `200 OK` — returns the created Student object

---

#### Login
```
POST /students/login
Content-Type: application/json

{
  "email": "john@dorm.com",
  "password": "secret123"
}
```
**Response:** `200 OK` — returns the Student object  
**Response:** `401 Unauthorized` — if credentials are wrong

---

#### Get a student by ID
```
GET /students/{id}
```
**Response:** `200 OK` — returns the Student object

---

### Washing Machines

#### Get all machines
```
GET /washmachines/all
```
**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "WM1",
    "status": "Available",
    "timeRemaining": null,
    "users": null,
    "isOpen": true
  }
]
```

Possible status values: `Available`, `In Use`, `Out of Order`

---

### Slots

#### Get available slots for a machine today
```
GET /slots/available?machineId=1
```
**Response:** `200 OK`
```json
[
  { "startTime": "10:00", "endTime": "10:15" },
  { "startTime": "10:15", "endTime": "10:30" },
  { "startTime": "10:30", "endTime": "10:45" }
]
```

Slots are 15-minute increments from 06:00 to 23:00. Past slots are excluded. Slots blocked by existing reservations (wash duration + 15-minute cleanup buffer) are excluded.

---

### Reservations

#### Create a reservation
```
POST /reservations/create
Content-Type: application/json

{
  "studentId": 1,
  "machineId": 1,
  "startTime": "10:00",
  "endTime": "10:45",
  "date": "2026-04-01",
  "washType": "Cotton",
  "washDuration": 45
}
```
**Response:** `200 OK` — returns the created Reservation object  
**Response:** `409 Conflict` — if slot is already taken, student not found, or machine not found

---

#### Get all reservations
```
GET /reservations/all
```
**Response:** `200 OK` — returns array of all active reservations with nested student and machine objects

---

## Database Schema

### Student
| Column     | Type    | Notes          |
|------------|---------|----------------|
| studentId  | INT     | PK, auto       |
| vorname    | VARCHAR | First name     |
| nachname   | VARCHAR | Last name      |
| email      | VARCHAR | Unique         |
| password   | VARCHAR | Plain text ⚠️  |
| balance    | INT     | Default 0      |

### Washmachine
| Column        | Type    | Notes                        |
|---------------|---------|------------------------------|
| machineId     | INT     | PK, auto                     |
| machineNr     | VARCHAR | Machine label e.g. "WM1"     |
| status        | VARCHAR | Available / In Use / Out of Order |
| timeRemaining | INT     | Minutes remaining (nullable) |
| users         | VARCHAR | Comma-separated (nullable)   |
| isOpen        | BOOLEAN |                              |

### Reservation
| Column        | Type      | Notes                  |
|---------------|-----------|------------------------|
| reservationId | INT       | PK, auto               |
| date          | DATE      |                        |
| startTime     | VARCHAR   | e.g. "10:00"           |
| endTime       | VARCHAR   | e.g. "10:45"           |
| status        | VARCHAR   | Default "active"       |
| washType      | VARCHAR   | e.g. "Cotton"          |
| washDuration  | INT       | In minutes             |
| createdAt     | DATETIME  | Auto timestamp         |
| studentId     | INT       | FK → Student           |
| machineId     | INT       | FK → Washmachine       |

---

## Testing

### Run all tests
```bash
mvn test
```

### Test classes

| Class                          | Type        | What it tests                                      |
|-------------------------------|-------------|----------------------------------------------------|
| `ReservationRepositoryTest`   | Repository  | `isSlotTaken()`, `findByMachineIdAndDate()`        |
| `ReservationServiceTest`      | Unit        | Booking logic, conflict detection                  |
| `ReservationControllerTest`   | Unit        | Controller responses and error handling            |
| `ReservationFlowIntegrationTest` | Integration | Full HTTP flow via MockMvc                      |
| `SlotControllerTest`          | Unit        | Slot generation, blocking, ordering                |
| `StudentServiceTest`          | Unit        | Login, registration                                |
| `StudentControllerTest`       | Unit        | Student endpoints                                  |
| `WashmachineControllerTest`   | Unit        | Machine listing endpoint                           |

### Test configuration

Tests use an in-memory H2 database. The dialect is overridden via `@TestPropertySource` to avoid MySQL-specific syntax errors.

---

## Known Limitations

- **Passwords are stored as plain text** — hashing (e.g. BCrypt) should be added before any real deployment
- **No authentication/authorization** — any client can call any endpoint without being logged in
- **No input validation** — missing `@Valid` annotations means invalid data can reach the database
- **Slot availability is date-locked to today** — the `/slots/available` endpoint only returns slots for the current day; future date selection is not yet supported on the backend
- **No reservation cancellation endpoint** — cancellation would need a `PATCH /reservations/{id}/cancel` endpoint
