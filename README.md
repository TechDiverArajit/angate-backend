# 🎟️ AnGate — Event Ticket Booking Backend

A production-ready **event ticket booking backend** built with **Spring Boot**, designed to handle secure authentication, concurrent ticket bookings, online payments, QR-based ticket verification, and automated booking confirmations.

AnGate provides a complete REST API for managing users, events, bookings, payments, tickets, and administrative operations.

---

## 🚀 Live Project

**Frontend:** https://angate.vercel.app/

**Backend API:** https://angate-backend.onrender.com

**Frontend Repository:** https://github.com/TechDiverArajit/angate-frontend.git

---

## ✨ Features

### 🔐 Authentication & Authorization

* JWT-based authentication
* Spring Security integration
* Role-based authorization
* Secure password handling
* Protected REST endpoints
* Separate access levels for users and administrators

### 🎫 Event & Ticket Management

* Create and manage events
* Browse available events
* View event details
* Ticket inventory management
* Booking management
* Admin-only event operations

### ⚡ Concurrency Handling

AnGate uses **Optimistic Locking** to prevent ticket overselling when multiple users attempt to book the last available tickets simultaneously.

This ensures that concurrent booking requests cannot incorrectly reduce the ticket inventory below the available capacity.

```text
User A ──┐
         ├──> Booking Request ──> Database
User B ──┘                         │
                                   ↓
                         Optimistic Locking
                                   │
                     ┌─────────────┴─────────────┐
                     ↓                           ↓
                Booking succeeds           Conflict detected
                                            & request rejected
```

### 💳 Razorpay Payment Integration

* Integrated **Razorpay Payment Gateway**
* Online ticket payments
* Payment order creation
* Payment verification
* Booking confirmation after successful payment

### 📧 Email Confirmation

After a successful booking, users receive an automated confirmation email containing their booking details.

Email delivery is integrated using the **Brevo API**.

### 📱 QR Code Ticket System

* Unique QR code generated for every confirmed booking
* QR code associated with the user's ticket
* Admin-only QR scanning
* Ticket verification through QR scanning
* Helps prevent unauthorized ticket usage

### 🛡️ Security

* JWT authentication
* Role-based access control
* Protected admin endpoints
* Server-side validation
* Global exception handling
* Secure API architecture

### 📄 REST API

The backend exposes **20+ REST endpoints** covering:

* Authentication
* Users
* Events
* Bookings
* Payments
* Tickets
* QR verification
* Admin operations

---

# 🏗️ Tech Stack

| Technology         | Usage                          |
| ------------------ | ------------------------------ |
| ☕ Java             | Backend programming language   |
| 🌱 Spring Boot     | Backend framework              |
| 🔐 Spring Security | Authentication & authorization |
| 🎫 JWT             | Stateless authentication       |
| 🗄️ PostgreSQL     | Production database            |
| 🧩 Spring Data JPA | Database interaction           |
| 💳 Razorpay        | Payment gateway                |
| 📧 Brevo API       | Transactional emails           |
| 📱 ZXing           | QR code generation/scanning    |
| 📦 Maven           | Dependency management          |
| 🧪 Postman         | API testing                    |
| 🚀 Render          | Backend deployment             |

---

# 🏛️ Architecture

AnGate follows a layered backend architecture:

```text
                    ┌──────────────────────┐
                    │      React Client    │
                    └──────────┬───────────┘
                               │
                               │ REST API
                               ↓
                    ┌──────────────────────┐
                    │    Spring Boot API   │
                    └──────────┬───────────┘
                               │
                ┌──────────────┼──────────────┐
                ↓              ↓              ↓
          Controllers      Services      Security
                │              │              │
                └──────────────┼──────────────┘
                               ↓
                    ┌──────────────────────┐
                    │   Spring Data JPA    │
                    └──────────┬───────────┘
                               ↓
                    ┌──────────────────────┐
                    │      PostgreSQL       │
                    └──────────────────────┘

External Integrations:

Spring Boot ──→ Razorpay
Spring Boot ──→ Brevo API
Spring Boot ──→ QR Generation
```

---

# 📂 Project Structure

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── angate/
    │           ├── controller/
    │           ├── service/
    │           ├── repository/
    │           ├── entity/
    │           ├── dto/
    │           ├── exception/
    │           ├── config/
    │           └── ...
    │
    └── resources/
        └── application.properties

pom.xml
README.md
```

> Package names may differ depending on your actual project structure.

---

# 🔄 Booking Flow

A typical booking follows this flow:

```text
User
 │
 ↓
Login / Register
 │
 ↓
JWT Authentication
 │
 ↓
Browse Events
 │
 ↓
Select Tickets
 │
 ↓
Create Booking
 │
 ↓
Razorpay Payment
 │
 ↓
Payment Verification
 │
 ↓
Booking Confirmed
 │
 ├──→ QR Code Generated
 │
 └──→ Confirmation Email Sent
```

---

# ⚡ Optimistic Locking

One of the key backend challenges solved in AnGate is **concurrent ticket booking**.

For example, suppose an event has only **1 ticket remaining**.

Two users may attempt to purchase it at almost the same time.

Without concurrency control:

```text
Ticket Available = 1

User A → Booking → Available = 0
User B → Booking → Available = -1 ❌
```

AnGate uses JPA optimistic locking to prevent this.

```java
@Version
private Long version;
```

When concurrent modifications occur, the database detects the version conflict and prevents an invalid update.

Result:

```text
Ticket Available = 1

User A → Booking → SUCCESS
User B → Booking → CONFLICT ❌
```

This prevents **ticket overselling** under concurrent requests.

---

# 🔑 Environment Variables

Sensitive credentials are not hardcoded in the application.

Configure the following environment variables:

```env
DB_URL=your_postgresql_database_url
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

JWT_SECRET=your_jwt_secret

RAZORPAY_KEY_ID=your_razorpay_key
RAZORPAY_KEY_SECRET=your_razorpay_secret

BREVO_API_KEY=your_brevo_api_key
```

Example Spring Boot configuration:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update

Jwt.secretKey=${JWT_SECRET}

razorpay.key.id=${RAZORPAY_KEY_ID}
razorpay.key.secret=${RAZORPAY_KEY_SECRET}

server.port=${PORT:8080}
```

⚠️ **Never commit real credentials, API keys, JWT secrets, or database passwords to GitHub.**

---

# 🛠️ Getting Started

## 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
```

```bash
cd YOUR_REPOSITORY
```

---

## 2. Configure environment variables

Create your environment variables according to the configuration above.

For local development, make sure PostgreSQL is running and the database credentials are correctly configured.

---

## 3. Install dependencies

Using Maven:

```bash
./mvnw clean install
```

Windows:

```bash
mvnw.cmd clean install
```

---

## 4. Run the application

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

---

# 🧪 API Testing

The APIs can be tested using **Postman**.

Typical flow:

```text
Register
   ↓
Login
   ↓
Receive JWT
   ↓
Send JWT in Authorization Header
   ↓
Access Protected APIs
```

Authorization header:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# 🔐 API Access Model

| Feature            | User | Admin |
| ------------------ | :--: | :---: |
| Register           |   ✅  |   ✅   |
| Login              |   ✅  |   ✅   |
| View Events        |   ✅  |   ✅   |
| Book Tickets       |   ✅  |   ✅   |
| View Own Bookings  |   ✅  |   ✅   |
| Make Payment       |   ✅  |   ✅   |
| Generate Ticket QR |   ✅  |   ✅   |
| Create Event       |   ❌  |   ✅   |
| Update Event       |   ❌  |   ✅   |
| Delete Event       |   ❌  |   ✅   |
| Scan QR            |   ❌  |   ✅   |
| Verify Ticket      |   ❌  |   ✅   |
| Manage Events      |   ❌  |   ✅   |

---

# 📊 Core Backend Concepts Demonstrated

This project demonstrates practical backend engineering concepts including:

* RESTful API development
* Layered architecture
* DTO-based request/response handling
* Spring Data JPA
* Entity relationships
* PostgreSQL database integration
* JWT authentication
* Spring Security
* Role-based authorization
* Optimistic locking
* Transaction management
* Global exception handling
* Input validation
* Payment gateway integration
* QR code generation
* QR code verification
* Transactional email integration
* Environment-based configuration
* Production deployment

---

# 🚀 Deployment

The backend is deployed using **Render**.

Production architecture:

```text
                 ┌─────────────────┐
                 │  React Frontend │
                 │     Vercel      │
                 └────────┬────────┘
                          │
                          ↓
                 ┌─────────────────┐
                 │  Spring Boot   │
                 │     Render      │
                 └────────┬────────┘
                          │
              ┌───────────┼───────────┐
              ↓           ↓           ↓
         PostgreSQL    Razorpay    Brevo API
```

Environment variables are configured through the deployment platform rather than being stored in the repository.

---

# 📈 Future Improvements

Potential improvements include:

* Redis caching
* Refresh token mechanism
* Rate limiting
* Redis-based distributed locking
* Docker containerization
* CI/CD pipeline
* Advanced monitoring and logging
* Event search and filtering
* Promotional coupons
* Booking cancellation and refunds
* Admin analytics dashboard
* Horizontal scaling

---

# 🎯 Why AnGate?

AnGate was built to go beyond basic CRUD operations and demonstrate how a real-world ticket booking system can handle:

**Authentication → Authorization → Inventory → Concurrency → Payment → Ticket Generation → Email → QR Verification**

The project focuses on solving practical backend problems rather than simply implementing database CRUD operations.

---

## 👨‍💻 Author

**Arajit Debnath**

BCA Student | Java Backend Developer

### Tech Interests

* Java
* Spring Boot
* Spring Security
* PostgreSQL
* REST APIs
* React
* Docker
* DevOps

---

## ⭐ Support

If you found this project interesting, consider giving the repository a ⭐ on GitHub.

**Built with Java + Spring Boot + PostgreSQL ❤️**
