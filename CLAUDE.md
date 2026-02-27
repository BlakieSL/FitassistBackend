# CLAUDE.md

## Critical Rules

- NEVER write manual entity-to-DTO mapping — use MapStruct (`mapper/` package)
- NEVER add cache invalidation or side effects in service logic — publish a Spring `ApplicationEvent` (`event/` package)
- NEVER put authorization logic in controllers — use custom annotations (`annotation/` package: `@AdminOnly`, `@AccountOwnerOrAdmin`, etc.)
- NEVER add a new service without interface under `service/declaration/` AND implementation under `service/implementation/`
- NEVER use custom JPQL for filtered list endpoints — use JPA Specifications (`specification/` package)
- NEVER read or modify `.env.properties`
- Run `./gradlew format` before every commit — enforced in CI, will fail PRs

## Architecture

Spring Boot 4.0.3 / Java 25 monolith. `src/main/java/com/fitassist/backend/` organized by layer then domain.

| Concern | Technology |
|---|---|
| Database | MySQL 8.0 via Spring Data JPA / Hibernate |
| Cache L1 | Caffeine (in-process, 2 min TTL) |
| Cache L2 | Redis via Redisson (15 min TTL) |
| Search | Apache Lucene |
| Auth | Stateless JWT in HTTP-only cookies |
| Rate Limiting | Redisson (Redis-backed) |

Key packages:
- `annotation/` — security annotations for controller methods
- `auth/` — `JwtService`, `JwtAuthenticationFilter`, `BearerTokenFilter`, `RateLimitingFilter`
- `config/cache/` — `RedisCachingConfig`, `CacheNames`, `CacheKeys`
- `event/event/` + `event/listener/` — application events and handlers
- `model/` — 59 JPA entities organized by domain

## Commands

```bash
./gradlew bootRun          # Run on port 8000 (dev profile)
./gradlew test             # Run all tests (81% coverage enforced)
./gradlew build
./gradlew format           # Auto-fix formatting
./gradlew checkFormat      # Check only
./gradlew jib              # Build + push Docker image to ECR (destructive)
```

Local: `docker compose -f src/main/resources/docker-compose.yaml up -d` (MySQL :3308, Redis :6379)
Coverage report: `build/jacocoHtml/index.html`
In dev, S3 and SES are mocked — AWS credentials not required locally.

## Testing

JUnit 5 + Testcontainers. Integration tests under `src/test/java/com/fitassist/backend/integration/` mirror controller structure. Coverage target: 81% (JaCoCo enforced).

## Branching

- `master` → production (triggers ECS deploy on push)
- `dev` → integration branch; all PRs target here

## Context Compaction

Preserve: modified files + state, current branch, uncommitted work, test pass/fail state, active TODOs and implementation plan.

## API Reference

https://documenter.getpostman.com/view/34870574/2sBXcGEf3L
