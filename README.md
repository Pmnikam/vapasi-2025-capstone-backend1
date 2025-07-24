Home Loan Management Backend API
A RESTful backend application that enables users to apply for home loans online and allows admins to review and manage applications. This project simplifies the loan process by eliminating the need for in-person visits, while offering tools like EMI and eligibility calculators.

 Features
� User Features:
- Register and Login securely
- Apply for a home loan with income, personal, and property details
- Upload required documents
- Check loan application status
- Use EMI and Eligibility calculators

 Admin Features:
- Login with admin credentials
- View loan applications by status
- Update application status (e.g. Pending, Approved, Rejected)
- View all user applications

Technologies Used

- Java 17
- Spring Boot 3
- Spring Data JPA
- MySQL
- Swagger
- Lombok
- Maven
- JUnit & Mockito (for testing)
- Jacoco


Running the Project
1)Prerequisites

- Java 17+
- MySQL Server
- Maven 3+

2)Setup MySQL

-sql
CREATE DATABASE home_loan;

Test Cases
Controller layer tested using @SpringBootTest
Service layer unit tested with @SpringBootTest
Repository layer tested using @DataJpaTest
Validations tested(e.g : userAccount, LoanApplication, CustomerProfile, LoanAccount)

