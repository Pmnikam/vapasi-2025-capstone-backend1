# 🏠 Home Loan Application - Spring Boot Backend

##  Project Overview

Availing home loans in India is often a long and tedious process. Our **Home Loan Application** provides a **hassle-free, user-friendly online platform** where customers can:

- Apply for home loans
- Track loan application status
- Get eligibility and EMI estimates
- Avoid repeated visits to bank branches

The system is built as a **Web API (Spring Boot)** application that enables seamless communication between the client (frontend) and server.

---

##  Users

- **Admin** – Reviews and approves/rejects loan applications
- **Customer** – Applies for loans, tracks status, uploads documents

---

##  Tech Stack

| Layer               | Technology Used                                   |
|--------------------|---------------------------------------------------|
| Backend Framework  | Spring Boot                                       |
| REST API           | Spring Web (REST Controllers)                     |
| ORM / Persistence  | Spring Data JPA, Hibernate                        |
| Database           | MySQL / H2                                        |
| Build Tool         | Maven                                             |
| Security           | Spring Security (JWT, BCrypt)                     |
| Testing            | JUnit 5, Mockito, MockMvc , Jacoco(Code Coverage) |
| Logging            | SLF4J, Logback                                    |
| Documentation      | Swagger/OpenAPI (optional)                        |

---

##  ER-Diagram
<img src="er-diagram.svg" alt="ER Diagram" width="600"/>

---

##  Modules & Features

### 1. **Home Page**
- Introduction to the platform
- Links for login/registration
- EMI & Eligibility calculators

### 2. **User Authentication**
- Customer Registration (Email & Password)
- Secure Login using Spring Security
- JWT Token-based Authentication

### 3. **Loan Application**
- **Form Sections:**
    - Personal Details (Name, DOB, Aadhar, PAN)
    - Property Details (Location, Cost, Type)
    - Income Details (Monthly income)
    - Document Uploads
- **Post-submission:**
    - Checklist Generation
    - Application ID Generation
    - Appointment for Document Verification

### 4. **Loan Tracker**
- Track application status using:
    - Application ID
    - Date of Birth
- **Statuses:**
    - Sent for Verification
    - Verified & Awaiting Approval
    - Approved / Rejected

### 5. **Admin Panel**
- View all submitted applications
- Approve or reject loan requests
- View customer loan profiles
- Create accounts after approval

### 6. **Account Creation**
- After loan approval:
    - Auto-generate Loan Account Number
    - Set Disbursed Loan Amount

### 7. **Calculators**
- **Eligibility Calculator:**
    - `Eligible Loan Amount = 60 × (0.6 × Monthly Salary)`
- **EMI Calculator:**
    - `EMI = P × R × ((1+R)^N) / ((1+R)^N - 1)`
    - R = Rate / 12 / 100, P = Principal, N = Tenure in months

---

##  API Endpoints (Sample)

| HTTP Method | Endpoint                             | Description                          |
|-------------|--------------------------------------|--------------------------------------|
| `POST`      | `/users/signup`                      | Register a new user                  |
| `POST`      | `/users/login`                       | Login and receive JWT token          |
| `POST`      | `/users/{userId}/loan`               | Submit loan application              |
| `GET`       | `/users/{userId}/loan/{appId}`       | Get loan application by ID           |
| `GET`       | `/users/{userId}/loan/check-active`  | Check active loan status             |
| `GET`       | `/admin/users`                       | View all customer loan info (admin)  |
| `PUT`       | `/admin/users/{cid}/loan/{loanId}`   | Approve/Reject loan application      |

---

##  Testing

- Unit testing with **JUnit 5**
- Controller testing using **MockMvc**
- Service layer tested using **Mockito**
- Code coverage via **JaCoCo**

---

##  How to Run

### Prerequisites:
- Java 17+
- Maven 3.6+
- MySQL or H2 DB

### Steps:

```bash
# Clone the repo
git clone https://github.com/Pmnikam/vapasi-2025-capstone-backend1.git
cd vapasi-2025-capstone-backend1

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Swagger URL
http://localhost:8080/swagger-ui.html

# ER-Diagram Reference
https://www.mermaidchart.com/play?utm_source=mermaid_live_editor&utm_medium=toggle#pako:eNqNktFugjAUhl-l4d4X8K4Dl5gxISB3S8yRNtoEziFt0Szgu69MptOyae_4fs45_3_aLihJyGAeSB0p2GmoP5C5U-SLbMPDMClWa9b3s1nfs7DI18m742mWvC7jBZuzPZjz_5421FDH4oSvNjxN42XI18tk5WqgaSoln6ob588ZHdEVTFjrzmw4MeGOKcHStyvLrVaOItTSg7IGVXm0AWOOpIUnaKp-9Xgh9wnIlOGlVYdROf149GI98BlTCVUEVjJBW280CKGlMR4vlf30oLGujR9L4XDRPt8TTlDAu0DePT4IFFG7rSSrCJDX1KL1ZgxS2uqGjJzM0Jqp9Xw_nhKsIowuOe9cPvc0_nE4SgqtdHu32c1Cr16cS22jP0SJ4lYam8paXUwHpy_DHQfK

```

###  Contributed by

Team: VapasiWorks 2025 Batch

