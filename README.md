# WaschSalon — Student Laundry Booking System

A full-stack web application that lets students in a dormitory book washing machines and dryers online — no more trips to the laundry room to check availability.

Live demo: [waschsalon.vercel.app](https://waschsalon.vercel.app)  
Backend API: [waschsalon.onrender.com](https://waschsalon.onrender.com)

---

## Features

- View all washing machines and dryers with live status
- Browse available 15-minute time slots (06:00–23:00), with occupied windows and cleanup buffers automatically blocked
- Book a slot with a chosen wash type and pay from an in-app balance
- Load balance via Stripe (credit card)
- Cancel active reservations and receive a full refund
- JWT-based authentication with role-based access control (`STUDENT` / `ADMIN`)
- Passwords hashed with BCrypt — never stored or returned in plaintext
- All monetary values stored as `BigDecimal` — no floating-point precision issues

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21 + Spring Boot 3.5 |
| Frontend | React 18 |
| Database | Supabase/PostgreSQL (production), H2 (tests) |
| ORM | Spring Data JPA + Hibernate |
| Auth | JWT (JJWT 0.12) + BCrypt |
| Payments | Stripe Java SDK |
| Deployment | Docker + Render (backend), Vercel (frontend), Supabase (database) |

---

## Architecture

The backend follows **Onion Architecture** — three concentric layers where dependencies only point inward.

```
domain/          ← Entities, repository interfaces. No framework imports.
application/     ← Business logic, service interfaces + implementations.
infrastructure/  ← Spring controllers, JWT security, HTTP config.
exception/       ← Typed exception hierarchy (NotFoundException, ConflictException, etc.)
```

The `domain` and `application` layers have zero knowledge of Spring MVC or HTTP. Swapping any infrastructure concern (controller, security, database driver) does not require touching business logic.

---

## API Endpoints

### Public — no token required

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/students/register` | Register a new student account |
| POST | `/students/login` | Authenticate — returns a JWT token |
| GET | `/washmachines/all` | List all machines and their current status |
| GET | `/slots/available?machineId={id}` | List available time slots for a machine today |

### Student — requires `STUDENT` role

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/students/{id}` | Get student profile |
| GET | `/students/{id}/balance` | Get current balance |
| POST | `/students/{id}/balance/load` | Load balance (minimum €5) |
| POST | `/reservations/create` | Book a time slot |
| GET | `/reservations/student/{id}` | List this student's reservations |
| DELETE | `/reservations/{id}/cancel?studentId=` | Cancel a reservation and get a refund |
| POST | `/payment/create-intent` | Create a Stripe PaymentIntent |

### Admin — requires `ADMIN` role

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/reservations/all` | List all reservations across all students |

---

## Running Locally

### Prerequisites

- Java 21
- Node.js 18+
- PostgreSQL running locally

### 1. Configure environment

Create a `.env` file in the project root:

```
DATABASE_URL=jdbc:postgresql://localhost:5432/waschmachine
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password
JWT_SECRET=a_secret_key_at_least_32_characters_long
JWT_EXPIRATION=86400000
STRIPE_SECRET_KEY=sk_test_...
```

### 2. Run the backend

```bash
./mvnw spring-boot:run
```

API available at `http://localhost:8080`.

### 3. Run the frontend

```bash
cd frontend
npm install
npm start
```

Frontend opens at `http://localhost:3000`.

---

## Running Tests

```bash
./mvnw test
```

40 tests — unit tests (services, controllers via MockMvc) and integration tests (full HTTP stack against an in-memory H2 database).

| Test Class | Type | Covers |
|-----------|------|--------|
| `ReservationRepositoryTest` | Repository | `isSlotTaken`, `findByMachineIdAndDate` |
| `ReservationServiceTest` | Unit | Booking logic, slot conflict, not-found cases |
| `ReservationControllerTest` | Unit | Controller HTTP responses |
| `ReservationFlowIntegrationTest` | Integration | Full HTTP flow with real DB layer |
| `SlotControllerTest` | Unit | Slot generation and blocking |
| `StudentServiceTest` | Unit | Registration, login, balance operations |
| `StudentControllerTest` | Unit | Student endpoints |
| `WashmachineControllerTest` | Unit | Machine listing |

---

## Database Schema

### Student

| Column | Type | Notes |
|--------|------|-------|
| studentId | INT | PK, auto-increment |
| vorname | VARCHAR | NOT NULL |
| nachname | VARCHAR | NOT NULL |
| email | VARCHAR | NOT NULL, UNIQUE |
| password | VARCHAR | BCrypt hash — never returned in API responses |
| balance | DECIMAL | Defaults to 0 |
| role | VARCHAR | `STUDENT` or `ADMIN` — defaults to `STUDENT` |

### Washmachine

| Column | Type | Notes |
|--------|------|-------|
| machineId | INT | PK, auto-increment |
| machineNr | VARCHAR | Machine label, e.g. `WM1` |
| status | VARCHAR | Available / In Use / Out of Order |
| timeRemaining | INT | Minutes left in current cycle, nullable |
| isOpen | BOOLEAN | Whether the machine is operational |
| type | VARCHAR | `washer` or `dryer`, defaults to `washer` |

### Reservation

| Column | Type | Notes |
|--------|------|-------|
| reservationId | INT | PK, auto-increment |
| createdAt | DATETIME | Set automatically on insert |
| date | DATE | NOT NULL |
| status | VARCHAR | `active` or `cancelled` |
| startTime | VARCHAR | Format: `HH:mm` |
| endTime | VARCHAR | Format: `HH:mm` |
| washType | VARCHAR | e.g. Quick Wash, Normal Wash |
| washDuration | INT | Duration in minutes |
| price | DECIMAL | |
| studentId | INT | FK → Student |
| machineId | INT | FK → Washmachine |

---

## Deployment

The production environment uses three separate services:

| Service | Provider | What it runs |
|---------|----------|-------------|
| Frontend | [Vercel](https://vercel.com) | React app — auto-deploys on push to main |
| Backend | [Render](https://render.com) | Spring Boot API packaged as a Docker container |
| Database | [Supabase](https://supabase.com) | Managed PostgreSQL instance |

**How they connect:**
- The React app on Vercel calls the Render backend via HTTPS. The backend URL is set as an environment variable in Vercel.
- The Spring Boot app on Render connects to Supabase using a PostgreSQL connection string set as an environment variable (`DATABASE_URL`). Supabase provides the connection string from its project dashboard.
- CORS on the backend is configured to allow requests from `waschsalon.vercel.app`.

**Docker:** The backend is packaged into a Docker container using the `Dockerfile` in the project root. Render pulls and runs this container automatically on each deploy.

---

## Security

- **Passwords** hashed with BCrypt (work factor 10). The raw password is never stored or sent in any API response.
- **JWT tokens** are stateless, signed with HMAC-SHA, expire after 24 hours, and carry the user's role as a claim. The role is loaded into the Spring `SecurityContext` on every request by `JwtFilter`.
- **Role-based access control** enforced at the method level with `@PreAuthorize`. Unauthenticated or under-privileged requests receive `401` or `403`.
- **Input validation** on all request DTOs using Jakarta Bean Validation (`@NotBlank`, `@NotNull`, `@Positive`, `@DecimalMin`). Validation errors return `400` with field-level detail.
- **CORS** restricted to known frontend origins.
- **Money** stored as `BigDecimal` throughout — no `double` or `float` anywhere in the financial path.
