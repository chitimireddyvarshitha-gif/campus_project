# Campus Event Management System — Spring Boot Backend

Production-grade Java Spring Boot 3 REST API backend connecting to MySQL for the **Campus Event Management System** React frontend.

---

## 1. Technologies & Stack

- **Language:** Java 17+
- **Framework:** Spring Boot 3.3.4
- **Security:** Spring Security 6 with JWT (JSON Web Tokens) & BCrypt password hashing
- **ORM / Persistence:** Spring Data JPA with Hibernate
- **Database:** MySQL 8.x
- **Build Tool:** Apache Maven
- **Validation:** Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Email`, etc.)
- **Utilities:** Project Lombok
- **Testing:** JUnit 5, Mockito, Spring Security Test, MockMvc

---

## 2. Directory Structure

```
backend/
├── pom.xml
├── README.md
├── API-CONTRACT.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/campusevent/management/
    │   │       ├── CampusEventManagementApplication.java
    │   │       ├── config/
    │   │       │   ├── SecurityConfig.java
    │   │       │   ├── CorsConfig.java
    │   │       │   └── DataInitializer.java
    │   │       ├── controller/
    │   │       │   ├── AuthController.java
    │   │       │   ├── EventController.java
    │   │       │   ├── RegistrationController.java
    │   │       │   ├── StudentController.java
    │   │       │   └── FacultyController.java
    │   │       ├── dto/
    │   │       │   ├── request/
    │   │       │   └── response/
    │   │       ├── entity/
    │   │       │   ├── User.java
    │   │       │   ├── Event.java
    │   │       │   ├── Registration.java
    │   │       │   ├── Role.java
    │   │       │   ├── EventCategory.java
    │   │       │   ├── EventStatus.java
    │   │       │   └── RegistrationStatus.java
    │   │       ├── exception/
    │   │       │   └── GlobalExceptionHandler.java
    │   │       ├── repository/
    │   │       │   ├── UserRepository.java
    │   │       │   ├── EventRepository.java
    │   │       │   └── RegistrationRepository.java
    │   │       ├── security/
    │   │       │   ├── JwtTokenProvider.java
    │   │       │   ├── JwtAuthenticationFilter.java
    │   │       │   ├── JwtAuthenticationEntryPoint.java
    │   │       │   └── CustomUserDetailsService.java
    │   │       └── service/
    │   │           ├── AuthService.java
    │   │           ├── EventService.java
    │   │           ├── RegistrationService.java
    │   │           ├── StudentService.java
    │   │           └── FacultyService.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/campusevent/management/
            ├── CampusEventManagementApplicationTests.java
            ├── service/
            │   ├── AuthServiceTest.java
            │   ├── EventServiceTest.java
            │   └── RegistrationServiceTest.java
            └── controller/
                ├── AuthControllerTest.java
                └── EventControllerTest.java
```

---

## 3. Database Setup (MySQL)

1. Open your MySQL client (MySQL Workbench, phpMyAdmin, or MySQL CLI):
```sql
CREATE DATABASE campus_event_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Update database credentials in `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_event_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Hibernate `spring.jpa.hibernate.ddl-auto=update` will automatically generate all tables (`users`, `events`, `registrations`), foreign keys, and indexes on startup.

---

## 4. How to Build & Run the Backend

### Prerequisites
- JDK 17 or JDK 21 installed (`java -version`)
- Maven 3.8+ installed (`mvn -version`)
- MySQL 8.x running on port `3306`

### Commands

1. **Navigate to the backend directory:**
```bash
cd backend
```

2. **Clean & Compile with Maven:**
```bash
mvn clean install
```

3. **Run Unit & Mockito Tests:**
```bash
mvn test
```

4. **Start the Spring Boot Application:**
```bash
mvn spring-boot:run
```

The server will initialize on port `8080`.
The `DataInitializer` bean automatically seeds the database with initial users and realistic events if empty!

---

## 5. Pre-Seeded Default Accounts

For instant testing, the application bootstraps:

| User Type | Name | Email | Password | Role |
| :--- | :--- | :--- | :--- | :--- |
| **Student** | Varshitha Chitimireddy | `varshitha.c@campus.edu` | `CampusPass@2026` | `STUDENT` |
| **Faculty** | Dr. Robert Vance | `r.vance@campus.edu` | `FacultyPass@2026` | `FACULTY` |

---

## 6. Connecting to the Existing React Frontend

1. Ensure the React frontend is running:
```bash
npm run dev
```
*(Runs on `http://localhost:3000`)*

2. In the React frontend, `src/services/api.ts` is configured with:
```typescript
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
```

3. If you wish to switch from local frontend state simulation to real Spring Boot calls, simply uncomment the Spring Boot API lines in:
- `src/services/authService.ts`
- `src/services/eventService.ts`
- `src/services/registrationService.ts`

Both the Request payloads and Response JSON schemas in Spring Boot match the TypeScript interfaces in `src/types/index.ts`!

---

## 7. Step-by-Step Postman Testing Guide

### Step 1: Student Login
- **POST** `http://localhost:8080/api/auth/login`
- **Body (raw JSON):**
```json
{
  "email": "varshitha.c@campus.edu",
  "password": "CampusPass@2026"
}
```
- **Copy the returned `token`** value for subsequent requests.

### Step 2: Browse Events
- **GET** `http://localhost:8080/api/events?category=TECHNICAL`
- No auth required.

### Step 3: Register for Event
- **POST** `http://localhost:8080/api/events/3/register`
- **Header:** `Authorization: Bearer <STUDENT_TOKEN>`
- Enrolls student in Event #3 with live seat decrement.

### Step 4: View Student Registrations
- **GET** `http://localhost:8080/api/students/1/registrations`
- **Header:** `Authorization: Bearer <STUDENT_TOKEN>`

### Step 5: Faculty Login
- **POST** `http://localhost:8080/api/auth/login`
- **Body:**
```json
{
  "email": "r.vance@campus.edu",
  "password": "FacultyPass@2026"
}
```

### Step 6: Create New Campus Event (Faculty Only)
- **POST** `http://localhost:8080/api/faculty/events`
- **Header:** `Authorization: Bearer <FACULTY_TOKEN>`
- **Body:**
```json
{
  "title": "Autonomous Drone Challenge 2026",
  "description": "Aerial robotics competition for students.",
  "category": "TECHNICAL",
  "eventDate": "2026-11-25",
  "eventTime": "10:00 AM",
  "venue": "Campus Athletics Pavilion",
  "organizer": "Department of Aerospace & Robotics",
  "capacity": 150,
  "registrationDeadline": "2026-11-20"
}
```

### Step 7: View Registered Student Roster
- **GET** `http://localhost:8080/api/faculty/events/1/registrations`
- **Header:** `Authorization: Bearer <FACULTY_TOKEN>`
- Returns all students registered for Hackathon 2026 with emails, phone numbers, and dates.
