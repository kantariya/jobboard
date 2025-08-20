# JobBoard – RESTful Job Posting & Networking API
**JobBoard** is a backend-only REST API built with **Spring Boot** that provides features for user authentication, job postings, and professional networking connections.  
It is designed for learning and practicing **Spring Boot + MySQL** fundamentals, with endpoints tested using **Postman**.
---
## ✨ Features
### 👤 User Authentication
- **Basic Authentication** (username + password)
- Secure storage with MySQL and JPA
### 💼 Job Postings
- Create new job posts
- Retrieve available job posts
### 🔗 User Connections
- Add professional connections between users
- View and manage user's connections
### 🛠️ Tech Stack
| Layer        | Technologies                  |
|--------------|-------------------------------|
| Framework    | Spring Boot                   |
| Database     | MySQL (JDBC, JPA) |
| Build Tool   | Maven                         |
| Testing      | Postman                       |
---
## ⚙️ Setup Instructions
### 1. Clone the Repository
```bash
git clone https://github.com/kantariya/jobboard.git
cd jobboard
```
### 2. Configure Database
Update your MySQL connection in application.properties:
```
spring.datasource.url=jdbc:mysql://localhost:3306/jobboard
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
spring.jpa.hibernate.ddl-auto=update
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# JPA Settings
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
### 3. Run the Application
```bash
mvn spring-boot:run
```
App starts at: http://localhost:8080
## 🔗 API Endpoints (Sample)
### User
- POST /users/register → Register new user
- POST /users/login → Login with credentials
### Job Posts
- POST /jobs → Create a job post
- GET /jobs → List all job posts
### Connections
- POST /connections/{userId} → Add connection
- GET /connections → View connections
## 🧪 Testing
All endpoints tested using **Postman**.
## 🚀 Future Improvements
- JWT-based authentication
- Search and filter for job posts
- Deployment to cloud (Render/AWS/Heroku)
