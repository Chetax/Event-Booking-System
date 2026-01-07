


# Event Management System

A full-stack **Event Management System** designed to manage events efficiently, from creation to registration.  
The backend is built using **Spring Boot**, while the frontend is developed using **React.js**.

This project follows real-world backend architecture practices and exposes REST APIs consumed by the frontend.

---

## Project Overview

The Event Management System allows organizers to create and manage events, while users can explore and register for available events.  
The system is designed with scalability and clean architecture in mind.

---

## Tech Stack

### Backend
- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- MySQL (or any relational database)
- Maven

### Frontend
- React.js
- JavaScript (ES6+)
- HTML5
- CSS3
- Axios (for API calls)

---

## System Architecture

```

React.js (Frontend)
|
|  REST API (JSON)
v
Spring Boot (Backend)
|
v
Database (MySQL)

````

- Frontend communicates with backend using RESTful APIs.
- Backend follows layered architecture:
  - Controller
  - Service
  - Repository

---

## Features

### User Features
- View all available events
- Register for events
- View event details

### Organizer/Admin Features
- Create new events
- Update event details
- Delete events
- Manage event status

---

## Backend Setup (Spring Boot)

### Prerequisites
- Java 17+ (or Java 11)
- Maven
- MySQL

### Steps to Run Backend
1. Clone the repository
   ```bash
   git clone <repository-url>
````

2. Open backend project in IDE (IntelliJ / Eclipse)

3. Configure database in `application.properties`

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/event_management
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

4. Run the application

   ```bash
   mvn spring-boot:run
   ```

5. Backend will start on:

   ```
   http://localhost:8080
   ```

---

## Frontend Setup (React.js)

### Prerequisites

* Node.js (v16+ recommended)
* npm or yarn

### Steps to Run Frontend

1. Navigate to frontend directory

   ```bash
   cd frontend
   ```

2. Install dependencies

   ```bash
   npm install
   ```

3. Start the React app

   ```bash
   npm start
   ```

4. Frontend will run on:

   ```
   http://localhost:3000
   ```

---

## API Overview (Backend)

Some key API endpoints exposed by Spring Boot:

* `GET /api/events` – Get all events
* `GET /api/events/{id}` – Get event by ID
* `POST /api/events` – Create a new event
* `PUT /api/events/{id}` – Update an event
* `DELETE /api/events/{id}` – Delete an event

(All APIs return JSON responses)

---

## Contributors

* **Backend Development**: You
* **Frontend Development**: Amit (React.js)

---

## Future Enhancements

* JWT-based authentication & authorization
* Role-based access (Admin / User)
* Event reminders & notifications
* Payment gateway integration
* Deployment using Docker & Cloud services

---

