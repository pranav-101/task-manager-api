# Task Manager API

A Spring Boot REST API for managing tasks with JWT authentication.

## Tech Stack

- Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Swagger/OpenAPI

## Requirements

- Java 17+
- Maven
- MySQL

## Setup

1. Clone the repo and navigate to the project folder

2. Configure your database in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/task_manager_db
spring.datasource.username=root
spring.datasource.password=your_password
```

3. Run the app:
```bash
mvn spring-boot:run
```

4. Open http://localhost:8080/swagger-ui.html to explore the API

## API Endpoints

### Auth
- `POST /api/auth/register` - Create account
- `POST /api/auth/login` - Get JWT token

### Tasks
- `GET /api/tasks` - List all tasks (supports `?status=`, `?priority=`, `?categoryId=` filters)
- `GET /api/tasks/{id}` - Get single task
- `POST /api/tasks` - Create task
- `PUT /api/tasks/{id}` - Update task
- `PATCH /api/tasks/{id}/status` - Quick status update
- `DELETE /api/tasks/{id}` - Delete task
- `GET /api/tasks/overdue` - Get overdue tasks
- `GET /api/tasks/due-today` - Get today's tasks
- `GET /api/tasks/statistics` - Get counts by status

### Categories
- `GET /api/categories` - List categories
- `POST /api/categories` - Create category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

## Quick Start

Register:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","email":"demo@test.com","password":"demo123"}'
```

Login and save the token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"demo","password":"demo123"}'
```

Create a task (replace `<token>` with your JWT):
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title":"My first task","priority":"HIGH","dueDate":"2024-12-31"}'
```

## Database

The app auto-creates tables on startup. Three main tables:

- `users` - accounts
- `tasks` - task data with priority (LOW/MEDIUM/HIGH) and status (PENDING/IN_PROGRESS/COMPLETED)
- `categories` - optional task grouping

## Project Structure

```
src/main/java/com/taskmanager/
├── config/          # Security, Swagger config
├── controller/      # REST endpoints
├── dto/             # Request/Response objects
├── entity/          # JPA entities
├── enums/           # Priority, Status
├── exception/       # Error handling
├── repository/      # Data access
├── security/        # JWT handling
└── service/         # Business logic
```

## Notes

- JWT tokens expire after 24 hours
- All endpoints except `/api/auth/**` require authentication
- Users can only see their own tasks and categories
