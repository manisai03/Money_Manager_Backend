💰 Money_Manager Backend

A RESTful API for managing personal finances. This backend handles user authentication, income & expense tracking, category management, email notifications, and provides secure endpoints for financial data operations.

🚀 Features:

=> User registration and login with JWT authentication

=> CRUD operations for incomes, expenses, and categories

=> Profile management (update user details, profile photo)

=> Email notifications for account activation

=> Secure API endpoints (CORS, password hashing)

=> Database integration with PostgreSQL

=> Dockerized backend for easy deployment

=> API testing via Postman Collection

🛠️ Tech Stack:

=> Language & Framework: Java, Spring Boot

=> Database: PostgreSQL

=> Authentication: JWT

=> Email Service: SMTP (Brevo/Sendinblue)

=> Containerization: Docker

=> Build Tool: Maven

=> Testing: JUnit

=> Other Tools: Postman, ngrok (for local testing)


📂 Project Structure:

money_manager/

│── src/

│   ├── main/java/com/example/money_manager

│   │   ├── controller/    # REST API endpoints

│   │   ├── service/       # Business logic

│   │   ├── repository/    # JPA repositories

│   │   ├── entity/        # Database entities

│   │   └── security/      # JWT & authentication

│   └── resources/

│       └── application.properties  # Config & environment

│── Dockerfile               # Docker setup

│── pom.xml                  # Dependencies

│── README.md


⚙️ Environment Configuration:

=> PostgreSQL Database

    spring.datasource.url=jdbc:postgresql://<your-db-host>:5432/<db-name>?sslmode=require
    
    spring.datasource.username=<db-username>
    
    spring.datasource.password=<db-password>
    
    spring.datasource.driver-class-name=org.postgresql.Driver
    
    spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
    
    spring.jpa.hibernate.ddl-auto=update

=> Email Configuration
    
    spring.mail.host=smtp-relay.brevo.com
    
    spring.mail.port=587
    
    spring.mail.username=<smtp-username>
    
    spring.mail.password=<smtp-password>
    
    spring.mail.properties.mail.smtp.auth=true
    
    spring.mail.properties.mail.smtp.starttls.enable=true
    
    spring.mail.protocol=smtp
    
    spring.mail.properties.mail.smtp.from=<from-email>

=> JWT & Server
    
    jwt.secret=<your-jwt-secret>
    
    server.port=8081
    
    money.manager.frontend.url=http://localhost:5173
    
    app.activation.url=<your-ngrok-or-production-url>


🐳 Running with Docker:

=> Build Docker Image
   
    docker build -t money_manager_backend .

=> Run Container
    
    docker run -p 8081:8082 --env-file .env money_manager_backend



📡 API Endpoints:

    Method	  Endpoint	                Description	Auth Required
1.  POST	   /api/auth/register	        Register a new user	❌
2.  POST	   /api/auth/login	          Login & get JWT token	❌
3.  GET	     /api/profile	              Get current user profile	✅
4.  PUT	     /api/profile	              Update profile	✅
5.  GET	     /api/categories	          Get all categories	✅
6.  POST	   /api/categories	          Create new category	✅
7.  PUT	     /api/categories/{id}	      Update category	✅
8.  DELETE	 /api/categories/{id}	    Delete category	✅
9.  GET	     /api/transactions	        Get all income/expense transactions	✅
10. POST	   /api/transactions	        Add new transaction	✅
11. PUT	     /api/transactions/{id}	    Update transaction	✅
12. DELETE	 /api/transactions/{id}	  Delete transaction	✅


🧪 Testing:

=> Run unit & integration tests:
      mvn test


📦 Deployment:

=> Hosted on: Render Cloud

=> Deployment method: Docker container

=> Primary URL: https://money-manager-backend-10.onrender.com

=> Base URL for API endpoints: https://money-manager-backend-10.onrender.com/api/...
