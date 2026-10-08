# Campus Event Management System — REST API Contract

This document defines the REST API contract implemented by the Spring Boot backend (`http://localhost:8080/api`) to interface seamlessly with the existing React TypeScript frontend.

---

## 1. Overview & General Standards

- **Base URL:** `http://localhost:8080/api`
- **Authentication:** HTTP Header `Authorization: Bearer <JWT_TOKEN>`
- **Content-Type:** `application/json`
- **Error Response Format (GlobalExceptionHandler):**
```json
{
  "success": false,
  "message": "Detailed description of error",
  "timestamp": "2026-10-08T04:30:00"
}
```

---

## 2. Authentication Endpoints

### 2.1 Student Registration
- **Method:** `POST`
- **URL:** `/api/auth/register` (also `/api/auth/student/register`)
- **Auth Required:** No
- **Role:** Public
- **Request Body:**
```json
{
  "name": "Varshitha Chitimireddy",
  "email": "varshitha.c@campus.edu",
  "password": "CampusPass@2026",
  "phone": "+1 (555) 234-8901",
  "department": "Computer Science & Engineering",
  "year": "3rd Year (Junior)",
  "role": "STUDENT"
}
```
- **Success Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "name": "Varshitha Chitimireddy",
    "email": "varshitha.c@campus.edu",
    "role": "STUDENT",
    "phone": "+1 (555) 234-8901",
    "department": "Computer Science & Engineering",
    "year": "3rd Year (Junior)",
    "designation": null,
    "avatar": "https://..."
  }
}
```
- **Error Responses:**
  - `400 Bad Request`: Validation failure (blank name, invalid email, password < 6 chars)
  - `409 Conflict`: Duplicate email (`An account with email ... is already registered.`)

---

### 2.2 User Login (Student & Faculty)
- **Method:** `POST`
- **URL:** `/api/auth/login` (also `/api/auth/student/login`, `/api/auth/faculty/login`)
- **Auth Required:** No
- **Role:** Public
- **Request Body:**
```json
{
  "email": "varshitha.c@campus.edu",
  "password": "CampusPass@2026",
  "rememberMe": true
}
```
- **Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "name": "Varshitha Chitimireddy",
    "email": "varshitha.c@campus.edu",
    "role": "STUDENT",
    "phone": "+1 (555) 234-8901",
    "department": "Computer Science & Engineering",
    "year": "3rd Year (Junior)",
    "designation": null,
    "avatar": "https://..."
  }
}
```
- **Error Responses:**
  - `401 Unauthorized`: Invalid credentials

---

### 2.3 Get Current Authenticated Profile
- **Method:** `GET`
- **URL:** `/api/auth/me`
- **Auth Required:** Yes
- **Role:** Authenticated (STUDENT or FACULTY)
- **Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Varshitha Chitimireddy",
  "email": "varshitha.c@campus.edu",
  "role": "STUDENT",
  "phone": "+1 (555) 234-8901",
  "department": "Computer Science & Engineering",
  "year": "3rd Year (Junior)",
  "designation": null,
  "avatar": "https://..."
}
```
- **Error Responses:** `401 Unauthorized`

---

## 3. Public & Shared Event Endpoints

### 3.1 Get All Events (With Search & Filters)
- **Method:** `GET`
- **URL:** `/api/events`
- **Query Parameters (Optional):**
  - `search`: String (searches title, description, venue, organizer)
  - `category`: `TECHNICAL` | `CULTURAL` | `SPORTS` | `ACADEMIC` | `OTHER`
  - `venue`: String
  - `status`: `UPCOMING` | `ONGOING` | `COMPLETED` | `CANCELLED`
- **Auth Required:** No
- **Role:** Public
- **Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Campus Hackathon 2026",
    "description": "A 24-hour inter-collegiate coding marathon...",
    "category": "Technical",
    "eventDate": "2026-10-14",
    "date": "2026-10-14",
    "eventTime": "09:00 AM",
    "time": "09:00 AM",
    "venue": "Alan Turing Innovation Hub, Block C",
    "organizer": "Department of Computer Science & Engineering",
    "capacity": 200,
    "registeredCount": 178,
    "availableSeats": 22,
    "registrationDeadline": "2026-10-10",
    "deadline": "2026-10-10",
    "imageUrl": "/src/assets/images/event_campus_hackathon_1790559795018.jpg",
    "status": "Registration Open"
  }
]
```

---

### 3.2 Get Event By ID
- **Method:** `GET`
- **URL:** `/api/events/{eventId}`
- **Auth Required:** No
- **Role:** Public
- **Success Response (200 OK):**
```json
{
  "id": 1,
  "title": "Campus Hackathon 2026",
  "description": "A 24-hour inter-collegiate coding marathon...",
  "category": "Technical",
  "eventDate": "2026-10-14",
  "date": "2026-10-14",
  "eventTime": "09:00 AM",
  "time": "09:00 AM",
  "venue": "Alan Turing Innovation Hub, Block C",
  "organizer": "Department of Computer Science & Engineering",
  "capacity": 200,
  "registeredCount": 178,
  "availableSeats": 22,
  "registrationDeadline": "2026-10-10",
  "deadline": "2026-10-10",
  "imageUrl": "/src/assets/images/event_campus_hackathon_1790559795018.jpg",
  "status": "Registration Open"
}
```
- **Error Responses:** `404 Not Found` if event does not exist

---

## 4. Student Endpoints

### 4.1 Register for Event
- **Method:** `POST`
- **URL:** `/api/events/{eventId}/register` (also `/api/registrations`)
- **Auth Required:** Yes
- **Role:** `STUDENT`
- **Success Response (201 Created):**
```json
{
  "id": 101,
  "eventId": 1,
  "eventTitle": "Campus Hackathon 2026",
  "eventDate": "2026-10-14",
  "eventTime": "09:00 AM",
  "eventVenue": "Alan Turing Innovation Hub, Block C",
  "studentId": 1,
  "studentName": "Varshitha Chitimireddy",
  "studentEmail": "varshitha.c@campus.edu",
  "studentPhone": "+1 (555) 234-8901",
  "registrationDate": "2026-10-08",
  "status": "Registered"
}
```
- **Error Responses:**
  - `400 Bad Request`: Event cancelled or registration deadline passed
  - `400 Bad Request`: Event capacity full (`EventFullException`)
  - `409 Conflict`: Student already registered for this event (`DuplicateResourceException`)
  - `404 Not Found`: Event or student ID not found

---

### 4.2 Get Student Registrations
- **Method:** `GET`
- **URL:** `/api/students/{studentId}/registrations` (also `/api/registrations/student/{studentId}`)
- **Auth Required:** Yes
- **Role:** `STUDENT` or `FACULTY`
- **Success Response (200 OK):**
```json
[
  {
    "id": 101,
    "eventId": 1,
    "eventTitle": "Campus Hackathon 2026",
    "eventDate": "2026-10-14",
    "eventTime": "09:00 AM",
    "eventVenue": "Alan Turing Innovation Hub, Block C",
    "studentId": 1,
    "studentName": "Varshitha Chitimireddy",
    "studentEmail": "varshitha.c@campus.edu",
    "studentPhone": "+1 (555) 234-8901",
    "registrationDate": "2026-10-08",
    "status": "Registered"
  }
]
```

---

### 4.3 Cancel Student Registration
- **Method:** `DELETE` (also `PUT /api/registrations/{registrationId}/cancel`)
- **URL:** `/api/registrations/{registrationId}`
- **Auth Required:** Yes
- **Role:** `STUDENT` (owner) or `FACULTY`
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Registration successfully cancelled",
  "timestamp": "2026-10-08T04:30:00"
}
```
- **Error Responses:**
  - `400 Bad Request`: Registration already cancelled or unauthorized student attempt
  - `404 Not Found`: Registration ID not found

---

### 4.4 Get Student Dashboard Data
- **Method:** `GET`
- **URL:** `/api/students/{studentId}/dashboard`
- **Auth Required:** Yes
- **Role:** `STUDENT`
- **Success Response (200 OK):**
```json
{
  "upcomingEventsCount": 8,
  "myRegistrationsCount": 2,
  "completedEventsCount": 0,
  "availableEventsCount": 6,
  "upcomingRegisteredEvents": [ ... ],
  "recommendedEvents": [ ... ]
}
```

---

### 4.5 Get & Update Student Profile
- **Method:** `GET` / `PUT`
- **URL:** `/api/students/{studentId}/profile`
- **Auth Required:** Yes
- **Role:** `STUDENT`
- **PUT Body:**
```json
{
  "name": "Varshitha Chitimireddy",
  "phone": "+1 (555) 234-8901",
  "department": "Computer Science & Engineering",
  "year": "3rd Year (Junior)",
  "avatar": "https://..."
}
```

---

## 5. Faculty Management Endpoints

### 5.1 Faculty Dashboard
- **Method:** `GET`
- **URL:** `/api/faculty/dashboard`
- **Auth Required:** Yes
- **Role:** `FACULTY`
- **Success Response (200 OK):**
```json
{
  "totalEvents": 8,
  "upcomingEvents": 8,
  "totalRegistrations": 2243,
  "completedEvents": 0,
  "categorySummary": [
    {
      "category": "Technical",
      "count": 2,
      "registrations": 528,
      "capacity": 550,
      "fillRate": 96
    }
  ],
  "recentEvents": [ ... ]
}
```

---

### 5.2 Create New Event
- **Method:** `POST`
- **URL:** `/api/faculty/events` (also `POST /api/events`)
- **Auth Required:** Yes
- **Role:** `FACULTY`
- **Request Body:**
```json
{
  "title": "Quantum Computing Symposium 2026",
  "description": "A comprehensive faculty keynote on qubit stabilization.",
  "category": "TECHNICAL",
  "eventDate": "2026-11-18",
  "eventTime": "10:00 AM",
  "venue": "Physics Lecture Auditorium",
  "organizer": "Department of Physics & Computing",
  "capacity": 150,
  "registrationDeadline": "2026-11-15",
  "imageUrl": "/src/assets/images/event_tech_summit_1790559818879.jpg",
  "status": "UPCOMING"
}
```
- **Success Response (201 Created):** Full `EventResponse`

---

### 5.3 Update Event
- **Method:** `PUT`
- **URL:** `/api/faculty/events/{eventId}` (also `PUT /api/events/{eventId}`)
- **Auth Required:** Yes
- **Role:** `FACULTY`
- **Request Body:** Partial or complete event fields
- **Success Response (200 OK):** Full updated `EventResponse`

---

### 5.4 Delete Event
- **Method:** `DELETE`
- **URL:** `/api/faculty/events/{eventId}` (also `DELETE /api/events/{eventId}`)
- **Auth Required:** Yes
- **Role:** `FACULTY`
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Event successfully deleted from campus catalog",
  "timestamp": "2026-10-08T04:30:00"
}
```

---

### 5.5 View Registered Students for an Event
- **Method:** `GET`
- **URL:** `/api/faculty/events/{eventId}/registrations`
- **Auth Required:** Yes
- **Role:** `FACULTY`
- **Success Response (200 OK):**
```json
[
  {
    "registrationId": 101,
    "studentId": 1,
    "studentName": "Varshitha Chitimireddy",
    "email": "varshitha.c@campus.edu",
    "phone": "+1 (555) 234-8901",
    "registrationDate": "2026-09-20",
    "status": "Registered"
  }
]
```
