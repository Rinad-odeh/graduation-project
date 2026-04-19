# Baligh API - Mobile Backend

A Spring Boot REST API for a citizen issue-reporting system that allows people to report problems (infrastructure, public services, etc.) to government and municipal organizations.

---

## What It Does

Citizens can submit complaints or issues (e.g., broken roads, power outages) and track their status. Organizations receive and manage these issues, updating their progress. Admins oversee the entire system.

---

## Tech Stack

- **Java 17** + **Spring Boot 3.2.5**
- **Spring Data JPA** + **Hibernate** (ORM)
- **Spring Security** (authentication placeholder — JWT ready)
- **MySQL** (development) / **PostgreSQL** (production)
- **Expo Push Notifications** (mobile push alerts)
- **Lombok** (code generation)
- **Maven** (build tool)

---

## User Roles

| Role | Description |
|------|-------------|
| `USER` | Citizen who submits and tracks issues |
| `ORG_MEMBER` | Organization representative who manages issues |
| `ADMIN` | System administrator with full access |

---

## Issue Lifecycle

```
SUBMITTED → UNDER_REVIEW → IN_PROGRESS → RESOLVED
                                       ↘ ON_HOLD
                                       ↘ REJECTED
```

---

## Main API Endpoints

| Area | Base Path | Description |
|------|-----------|-------------|
| Auth | `/api/v1/auth` | Login & registration |
| Issues | `/api/v1/issues` | Create, track, and manage issues |
| Categories | `/api/v1/categories` | Issue categories (admin managed) |
| Organizations | `/api/v1/organizations` | Org registration & approval workflow |
| Users | `/api/v1/users` | User profile management |
| Notifications | `/api/v1/notifications` | Push notification history |
| Stats | `/api/v1/stats` | Analytics for admins and organizations |

Authentication is header-based (`X-User-Id`) with JWT integration planned.

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0 (for development)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Sara-Domaidi/mobileBackend.git
   cd mobileBackend
   ```

2. Create the MySQL database:
   ```sql
   CREATE DATABASE balighdb;
   ```

3. Configure credentials in `src/main/resources/application-dev.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

4. Run the app:
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

---

## Project Structure

```
src/main/java/com/baligh/backend/
├── controller/     # REST endpoints
├── service/        # Business logic
├── model/          # JPA entities
├── repository/     # Data access layer
├── dto/            # Request/Response objects
├── config/         # Security & app configuration
├── exception/      # Error handling
└── util/           # Helpers
```

---

## Key Features

- Phone-based authentication (no passwords)
- Organization approval workflow (PENDING → APPROVED/REJECTED)
- File attachments on issues (stored locally under `uploads/`)
- Expo push notifications sent on issue updates
- Full audit trail of status changes
- Arabic language support for categories and notifications
- Pagination on all list endpoints

---

## Configuration Profiles

| Profile | Database | DDL Mode |
|---------|----------|----------|
| `dev` | MySQL (`localhost:3306/balighdb`) | `update` |
| `prod` | PostgreSQL (via env vars) | `validate` |

Switch profiles with:
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```
