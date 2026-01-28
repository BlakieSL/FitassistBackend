# FitAssist Backend

A fitness REST API built with Spring Boot 3.5 and Java 25.

## Tech Stack

| Category | Technology                   |
|----------|------------------------------|
| **Framework** | Spring Boot 3.5.1            |
| **Language** | Java 25                      |
| **Database** | MySQL 8.0                    |
| **Caching** | Redis + Caffeine             |
| **Cloud** | AWS (ECS, ECR, S3, SES, RDS) |
| **Container** | Docker (Jib)                 |
| **CI/CD** | GitHub Actions               |

## Features

### Core Functionality
- **User Management** - Registration, authentication, profile management
- **Recipe Management** - Create, update, share recipes with ingredients and instructions
- **Workout Plans** - Custom workout plans with exercises, sets, and rest periods
- **Food Tracking** - Daily food intake with macro calculations
- **Activity Tracking** - Daily activity logging with calorie burn calculations
- **Forum** - Community threads and comments with moderation
- **Search** - Full-text search powered by Apache Lucene

### Security
- **JWT Authentication** - Access and refresh token flow with Nimbus JOSE JWT
- **BCrypt Password Hashing** - Secure password storage
- **Rate Limiting** - Redis-backed rate limiting with Redisson
- **CORS Configuration** - Configurable cross-origin resource sharing
- **Password Reset** - Email-based password recovery via AWS SES

### API Features
- **JSON Patch** - Partial updates via RFC 6902 JSON Patch
- **Pagination** - Spring Data pagination with DTO serialization
- **Dynamic Filtering** - JPA Specifications for flexible querying
- **Validation** - Jakarta Bean Validation with custom validators
- **i18n** - Internationalized error messages (EN, PL, RU)
- **Media Upload** - AWS S3 integration for file storage

## Architecture

```
src/main/java/com/fitassist/backend/
├── auth/                    # JWT, filters, rate limiting
├── config/                  # Spring configuration
├── controller/              # REST controllers
├── dto/                     # Request/Response DTOs
├── event/                   # Domain events and listeners
├── exception/               # Global exception handling
├── mapper/                  # MapStruct mappers
├── model/                   # JPA entities
├── repository/              # Spring Data repositories
├── service/                 # Business logic
├── specification/           # JPA Specifications for filtering
└── validation/              # Custom validators
```

### Key Design Patterns
- **DTO Pattern** - Separate DTOs for create, update, and response
- **MapStruct Mappers** - Compile-time entity-DTO mapping
- **Event-Driven** - Spring Events for decoupled operations

## Testing

### Test Infrastructure
- **Testcontainers** - Dockerized MySQL, Redis, and LocalStack (S3)
- **JUnit 5** - Modern testing framework
- **MockMvc** - Controller integration testing
- **Spring Security Test** - Authentication testing utilities

### Test Structure
```
src/test/java/
├── integration/
│   ├── config/          # Test configurations and mocks
│   ├── containers/      # Testcontainers setup
│   ├── test/
│   │   └── controller/  # Integration tests per controller
│   └── utils/           # Test utilities
└── unit/                # Unit tests
```

### Running Tests
```bash
# Run all tests with parallel execution
./gradlew test

# Tests run with optimized JVM settings:
# - G1GC garbage collector
# - 4GB max heap
# - String deduplication
# - Parallel execution (cores/2)
```

### Code Coverage
- **JaCoCo** - Automatic coverage reports generated after test runs
- Reports available at: `build/jacocoHtml/index.html`

## Code Quality

### Spring Java Format
Enforced code style using [Spring Java Format](https://github.com/spring-io/spring-javaformat):

```bash
# Check formatting
./gradlew checkFormat

# Auto-fix formatting
./gradlew format
```

### CI/CD Pipelines

| Workflow | Trigger | Description |
|----------|---------|-------------|
| `format-check.yml` | All branches | Validates code formatting |
| `main.yml` | master branch | Builds and runs tests |
| `deploy.yml` | master branch | Deploys to AWS ECS |

## Getting Started

### Prerequisites
- Java 25
- Docker (for Testcontainers)
- MySQL 8.0 (or use Testcontainers)
- Redis (or use Testcontainers)

### Docker Compose
For local development, you can use Docker Compose to run MySQL and Redis:

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f
```

### Local Development
```bash
# Clone the repository
git clone <repository-url>

# Start dependencies with Docker Compose
docker-compose up -d

# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

### Environment Variables
Create `.env.properties` with:
```properties
# AWS Credentials
AWS_ACCESS_KEY_ID=test
AWS_SECRET_ACCESS_KEY=test

# Database (RDS)
RDS_URL=jdbc:mysql://localhost:3306/test
RDS_USERNAME=admin
RDS_PASSWORD=admin

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# S3
S3_BUCKET_NAME=test

# JWT
JWT_SECRET_KEY=test

# Rate Limiting
RATE_LIMITER_USER_RATE=50
RATE_LIMITER_USER_INTERVAL=5
RATE_LIMITER_KEY_RATE=30
RATE_LIMITER_KEY_INTERVAL=5

# Application
FRONTEND_URL=http://localhost:3000
EMAIL_FROM=test@gmail.com
SPRING_PROFILES_ACTIVE=dev
```

## Deployment

### Docker Image (Jib)
```bash
# Build and push to ECR
./gradlew jib
```

Uses Eclipse Temurin JRE 25 as base image.

### AWS Infrastructure
- **ECS** - Container orchestration
- **ECR** - Container registry
- **RDS** - MySQL database
- **S3** - Media storage
- **SES** - Email delivery

## Dependencies

### Runtime
| Dependency | Purpose |
|------------|---------|
| Spring Boot Starter Web | REST API |
| Spring Boot Starter Data JPA | Database access |
| Spring Boot Starter Security | Authentication/Authorization |
| Spring Boot Starter Validation | Bean validation |
| Spring Boot Starter Data Redis | Redis caching |
| Spring Boot Starter Cache | Caching abstraction |
| Spring Boot Starter Actuator | Health checks & metrics |
| Redisson | Distributed rate limiting |
| Nimbus JOSE JWT | JWT handling |
| MapStruct | DTO mapping |
| Lombok | Boilerplate reduction |
| Caffeine | Local caching |
| Apache Lucene | Full-text search |
| AWS Spring Cloud (S3, SES) | AWS integration |

### Testing
| Dependency | Purpose |
|------------|---------|
| Testcontainers | Docker-based testing |
| Spring Security Test | Security testing |
| LocalStack | AWS service mocking |

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | User registration |
| POST | `/api/users/login` | User login |
| POST | `/api/users/refresh-token` | Refresh access token |
| POST | `/api/users/logout` | User logout |
| POST | `/api/password-reset/request` | Request password reset |
| POST | `/api/password-reset/reset` | Reset password |

### Resources
- `/api/users` - User management
- `/api/recipes` - Recipe CRUD
- `/api/foods` - Food database
- `/api/exercises` - Exercise library
- `/api/activities` - Activity tracking
- `/api/plans` - Workout plans
- `/api/threads` - Forum threads
- `/api/comments` - Thread comments
- `/api/media` - File uploads
- `/api/search` - Full-text search
- `/api/daily/*` - Daily tracking (food, activities)
- `/api/reports` - User statistics
