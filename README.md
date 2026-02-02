# Fitassist Backend

## About

A fitness REST API built with Java 25 and Spring Boot 3.5. Features include user management, recipe sharing, workout plans,
food/activity tracking, and a community forum with full-text global search.

**Tech Stack:** Spring Boot 3.5 | Java 25 | MySQL 8.0 | Redis | AWS (RDS, S3, ECS) | Docker

## Usage

### Installation

```bash
git clone <repository-url>
cd FitassistBackend
docker-compose up -d
./gradlew build
```

### Commands

| Command | Description |
|---------|-------------|
| `./gradlew bootRun` | Run the application |
| `./gradlew test` | Run all tests |
| `./gradlew build` | Build the project |
| `./gradlew jib` | Build and push Docker image to ECR |
| `./gradlew checkFormat` | Check code formatting |
| `./gradlew format` | Auto-fix formatting |

## Development

### Pre-Requisites

- Java 25
- Docker
- MySQL 8.0 (or use Docker Compose)
- Redis (or use Docker Compose)

### Development Environment

Create `.env.properties`:
```properties
RDS_URL=jdbc:mysql://localhost:3306/test
RDS_USERNAME=admin
RDS_PASSWORD=admin
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET_KEY=your-secret-key
SPRING_PROFILES_ACTIVE=dev
```

Start dependencies: `docker-compose up -d`

### File Structure

```
src/main/java/com/fitassist/backend/
├── auth/           # JWT, filters, rate limiting
├── config/         # Spring configuration
├── controller/     # REST controllers
├── dto/            # Request/Response DTOs
├── mapper/         # MapStruct mappers
├── model/          # JPA entities
├── repository/     # Spring Data repositories
├── service/        # Business logic
└── validation/     # Custom validators
```

### Build

```bash
./gradlew build
```

Coverage reports: `build/jacocoHtml/index.html`

### Deployment

Deployed to AWS ECS via GitHub Actions. Infrastructure includes ECS, ECR, RDS, S3, and SES.

```bash
./gradlew jib  # Build and push to ECR
```

## Community

### Contribution

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run `./gradlew checkFormat` and `./gradlew test`
5. Submit a pull request

### Branches

| Branch                               | Purpose     |
|--------------------------------------|-------------|
| `master`                             | Production  |
| `dev`                                | Development |
| `dev-backup-unused-features`         | Deprecated  |
| `test-github-actions-testcontainers` | Deprecated  |

### Guideline

- Follow [Spring Java Format](https://github.com/spring-io/spring-javaformat)
- Write tests for new features
- Keep commits focused and descriptive
