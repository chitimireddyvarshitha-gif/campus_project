# Campus Event Management System (Full-Stack)

A collegiate web application connecting a **React + TypeScript + Vite** frontend with a **Java Spring Boot 3 + Maven + MySQL** backend via REST APIs and JWT security.

---

## 🏛️ System Architecture

```
campus-event-management-system/
│
├── src/                                ← React + TypeScript + Vite Frontend
│   ├── assets/                         # College imagery and media
│   ├── components/                     # Collegiate UI components & layouts
│   ├── context/                        # AuthContext & EventContext providers
│   ├── data/                           # Fallback sample dataset for standalone preview
│   ├── pages/
│   │   ├── student/                    # Student portal (Login, Register, Dashboard, Events, Registrations, Profile)
│   │   └── faculty/                    # Faculty portal (Login, Dashboard, Add Event, Edit Event, Manage, Roster)
│   ├── services/
│   │   ├── api.ts                      # Central Axios client (http://localhost:8080/api + Bearer JWT interceptor)
│   │   ├── authService.ts              # Authentication & JWT session management
│   │   ├── eventService.ts             # Event catalog, search, and CRUD operations
│   │   ├── registrationService.ts      # Student enrollments & cancellation
│   │   ├── studentService.ts           # Student dashboard and profile APIs
│   │   └── facultyService.ts           # Faculty administration & attendee rosters
│   ├── types/                          # Shared TypeScript domain contracts
│   ├── App.tsx
│   └── main.tsx
│
├── backend/                            ← Java Spring Boot + Maven + MySQL Backend
│   ├── pom.xml                         # Spring Boot 3.3.4 dependencies
│   ├── README.md                       # Backend documentation & configuration
│   ├── API-CONTRACT.md                 # REST API specification & payloads
│   └── src/main/java/com/campusevent/management/
│       ├── config/                     # SecurityConfig, CorsConfig, DataInitializer
│       ├── controller/                 # Auth, Event, Registration, Student, Faculty REST controllers
│       ├── dto/                        # Request and response transfer objects
│       ├── entity/                     # JPA Hibernate entities (User, Event, Registration)
│       ├── repository/                 # Spring Data JPA repositories
│       ├── security/                   # JWT Token Provider, Auth Filter & EntryPoint
│       └── service/                    # Business logic implementations
│
└── package.json
```

---

## 🚀 Running the Application

### 1. Spring Boot Backend (`backend/`)

#### Prerequisites:
- Java 17 or higher
- Apache Maven 3.8+
- MySQL Server 8.x running on port `3306`

#### Database Setup:
1. Create MySQL database:
   ```sql
   CREATE DATABASE campus_event_management;
   ```
2. Configure database credentials in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/campus_event_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
3. Run the backend:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   *The backend will automatically start on `http://localhost:8080/api` and seed the initial users and campus events via `DataInitializer.java`.*

---

### 2. React Frontend (`root`)

1. Start the Vite development server:
   ```bash
   npm run dev
   ```
2. Open your browser at `http://localhost:3000` (or `http://localhost:5173`).

---

## 🔑 Default Seed Credentials

| Role | Email | Password | Details |
| :--- | :--- | :--- | :--- |
| **Student** | `varshitha.c@campus.edu` | `CampusPass@2026` | 3rd Year (Junior), Computer Science |
| **Faculty Coordinator** | `r.vance@campus.edu` | `FacultyPass@2026` | Associate Dean of Student Activities |

---

## 📡 API Endpoints Summary

- **Authentication:**
  - `POST /api/auth/register` — Student registration
  - `POST /api/auth/login` — Student/Faculty login (issues JWT token)
  - `GET /api/auth/me` — Authenticated profile verification
- **Events:**
  - `GET /api/events` — Catalog of events (filter by category, search, venue)
  - `GET /api/events/{id}` — Event details
  - `POST /api/faculty/events` — Create event (*Faculty only*)
  - `PUT /api/faculty/events/{id}` — Update event (*Faculty only*)
  - `DELETE /api/faculty/events/{id}` — Delete event (*Faculty only*)
- **Registrations:**
  - `POST /api/events/{eventId}/register` — Student event enrollment (*Student only*)
  - `GET /api/students/{studentId}/registrations` — Student registration history
  - `DELETE /api/registrations/{registrationId}` — Cancel event registration
  - `GET /api/faculty/events/{eventId}/registrations` — Attendee roster & CSV export (*Faculty only*)
