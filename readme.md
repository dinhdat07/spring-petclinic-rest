# Spring PetClinic REST backend

[![Java Build Status](https://github.com/spring-petclinic/spring-petclinic-rest/actions/workflows/maven-build-master.yml/badge.svg)](https://github.com/spring-petclinic/spring-petclinic-rest/actions/workflows/maven-build-master.yml)
[![Docker Build Status](https://github.com/spring-petclinic/spring-petclinic-rest/actions/workflows/docker-build.yml/badge.svg)](https://github.com/spring-petclinic/spring-petclinic-rest/actions/workflows/docker-build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=spring-petclinic_spring-petclinic-rest&metric=alert_status)](https://sonarcloud.io/dashboard?id=spring-petclinic_spring-petclinic-rest)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=spring-petclinic_spring-petclinic-rest&metric=coverage)](https://sonarcloud.io/dashboard?id=spring-petclinic_spring-petclinic-rest)

Backend-only, API-first version of Spring PetClinic. There is **no UI** in this module; the Angular client lives at https://github.com/spring-petclinic/spring-petclinic-angular.

## Monorepo layout (Maven)
```
petclinic-services/
  pom.xml               # parent
  common/               # shared DTO/events/util
  modulith-main/        # main API app (owners, vets, visits, appointments, IAM, etc.)
  scheduling-service/   # scheduling worker
  notification-service/ # notification worker
```

## What this backend provides
- REST API at `/petclinic/api/**` for owners, pets, vets, pet types, specialties, visits, appointments, and user management
- OpenAPI 3.1 contract (`src/main/resources/openapi.yml`) with Swagger UI at `/petclinic/swagger-ui.html`
- Security enabled by default (JWT login at `/petclinic/api/auth/login`), seeded demo users, and role-based self-service endpoints
- Default profile uses PostgreSQL; H2, HSQLDB, and MySQL profiles remain available
- Appointment events published to RabbitMQ with optional external Notification and Scheduling worker services
- Redis-backed caching (override `spring.cache.type=simple` if you prefer not to run Redis locally)

## Quick start
### Prerequisites
- Java 17+ and Maven 3.9+ (or the bundled `./mvnw`)
- Docker for infrastructure: PostgreSQL (default), RabbitMQ, Redis, and MailHog (for the email notification channel)

### Start infrastructure
```sh
docker compose --profile postgres up -d postgres rabbitmq mailhog
docker run -d --name petclinic-redis -p 6379:6379 redis:7
```

The default profile points to PostgreSQL on `localhost:5433`. If you use the compose service (exposes `5432`), override the datasource:
```sh
# Windows PowerShell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/petclinic"
$env:SPRING_DATASOURCE_USERNAME="petclinic"
$env:SPRING_DATASOURCE_PASSWORD="petclinic"

# macOS/Linux
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/petclinic
export SPRING_DATASOURCE_USERNAME=petclinic
export SPRING_DATASOURCE_PASSWORD=petclinic
```

### Build & run
```sh
cd petclinic-services
mvn clean package

# Main API app
java -jar modulith-main/target/modulith-main-3.4.3.jar

# Optional workers
java -jar scheduling-service/target/scheduling-service-3.4.3.jar
java -jar notification-service/target/notification-service-3.4.3.jar
```

Run tests (all modules):
```sh
cd petclinic-services
mvn test
```

### Service runtime (per module)
Minimal env examples (PowerShell style) before running JARs:

**Scheduling Service (PostgreSQL)**
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5433/petclinic_scheduling"
$env:SPRING_DATASOURCE_USERNAME="petclinic"
$env:SPRING_DATASOURCE_PASSWORD="petclinic"
$env:SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.postgresql.Driver"
$env:RABBITMQ_HOST="localhost"; $env:RABBITMQ_PORT="5672"; $env:RABBITMQ_USERNAME="guest"; $env:RABBITMQ_PASSWORD="guest"
java -jar scheduling-service/target/scheduling-service-3.4.3.jar
```

**Scheduling Service (H2, no external DB)**
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:h2:mem:scheduling;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
$env:SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.h2.Driver"
$env:SPRING_DATASOURCE_USERNAME="sa"; $env:SPRING_DATASOURCE_PASSWORD=""
$env:SPRING_JPA_HIBERNATE_DDL_AUTO="create-drop"
$env:RABBITMQ_HOST="localhost"; $env:RABBITMQ_PORT="5672"; $env:RABBITMQ_USERNAME="guest"; $env:RABBITMQ_PASSWORD="guest"
java -jar scheduling-service/target/scheduling-service-3.4.3.jar
```

**Notification Service (PostgreSQL)**
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5433/petclinic"
$env:SPRING_DATASOURCE_USERNAME="petclinic"
$env:SPRING_DATASOURCE_PASSWORD="petclinic"
$env:SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.postgresql.Driver"
$env:RABBITMQ_HOST="localhost"; $env:RABBITMQ_PORT="5672"; $env:RABBITMQ_USERNAME="guest"; $env:RABBITMQ_PASSWORD="guest"
$env:MAIL_HOST="localhost"; $env:MAIL_PORT="1025"
java -jar notification-service/target/notification-service-3.4.3.jar
```

**Notification Service (H2)**
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:h2:mem:notifications;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
$env:SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.h2.Driver"
$env:SPRING_DATASOURCE_USERNAME="sa"; $env:SPRING_DATASOURCE_PASSWORD=""
$env:SPRING_JPA_HIBERNATE_DDL_AUTO="create-drop"
$env:RABBITMQ_HOST="localhost"; $env:RABBITMQ_PORT="5672"; $env:RABBITMQ_USERNAME="guest"; $env:RABBITMQ_PASSWORD="guest"
$env:MAIL_HOST="localhost"; $env:MAIL_PORT="1025"
java -jar notification-service/target/notification-service-3.4.3.jar
```

### Run the API
```sh
./mvnw spring-boot:run
```
- Base URL: `http://localhost:9966/petclinic`
- Health: `http://localhost:9966/petclinic/actuator/health`
- Swagger UI: `http://localhost:9966/petclinic/swagger-ui.html`
- OpenAPI JSON: `http://localhost:9966/petclinic/v3/api-docs`

To use the in-memory H2 profile for quick demos:
```sh
$env:SPRING_PROFILES_ACTIVE="h2,spring-data-jpa"
$env:SPRING_CACHE_TYPE="simple"
./mvnw spring-boot:run
```
RabbitMQ is still required whenever appointments and messaging are exercised.

### Docker image
```sh
docker run -p 9966:9966 springcommunity/spring-petclinic-rest
```
Provide `SPRING_DATASOURCE_*`, `SPRING_PROFILES_ACTIVE`, and RabbitMQ/Redis settings to point the image at your infrastructure.

## Authentication and seeded users
Security is **enabled by default** (`petclinic.security.enable=true`). Authenticate via `POST /petclinic/api/auth/login` to obtain a JWT and pass it as `Authorization: Bearer <token>`.

| Username | Password | Roles |
|----------|----------|-------|
| `admin`  | `admin`  | `ADMIN`, `OWNER_ADMIN`, `VET_ADMIN` |
| `owner`  | `owner`  | `OWNER` |
| `vet`    | `vet`    | `VET` |

Owners can self-register through `POST /petclinic/api/auth/register`. Additional users can be added via `POST /petclinic/api/users`.

### Roles
- `OWNER_ADMIN`: back-office owner/pet/visit administration and all appointments (`/api/owners`, `/api/pets`, `/api/visits`, `/api/appointments/**`); can read `pettypes`.
- `VET_ADMIN`: manage vets and specialties (`/api/vets`, `/api/specialties`) and full CRUD on `pettypes`.
- `ADMIN`: system user management (`/api/users`).
- `OWNER`: self-service under `/api/me/**` (profile, appointments, visit history); read-only access to `pettypes`.
- `VET`: self-service under `/api/vets/me/**` (profile, assigned appointments, complete visits); read-only access to `pettypes`/`specialties`.

## Core API surface
All endpoints are prefixed with `/petclinic/api`.

- Owners & pets: create/update/delete owners, manage pets (`/api/owners`, `/api/pets`, `/api/pettypes`)
- Vets & specialties: manage veterinarians and their specialties (`/api/vets`, `/api/specialties`)
- Visits: schedule/update visits for pets (`/api/visits`)
- Appointments: queue, triage, confirm, and convert appointments to visits (`/api/appointments/**`)
- Self-service: owner endpoints under `/api/me/**`; vet endpoints under `/api/vets/me/**`
- Users & auth: login/registration plus admin user management (`/api/users`)

See the Swagger UI for the full contract and sample payloads.

## Database profiles
| Database | `spring.profiles.active` | Notes |
|----------|--------------------------|-------|
| PostgreSQL (default) | `postgres,spring-data-jpa` | Expects `jdbc:postgresql://localhost:5433/petclinic` by default; override `SPRING_DATASOURCE_URL`/`SPRING_DATASOURCE_USERNAME`/`SPRING_DATASOURCE_PASSWORD` as needed. |
| MySQL | `mysql,spring-data-jpa` | Start MySQL: `docker run -e MYSQL_ROOT_PASSWORD= -e MYSQL_ALLOW_EMPTY_PASSWORD=true -e MYSQL_USER=petclinic -e MYSQL_PASSWORD=petclinic -e MYSQL_DATABASE=petclinic -p 3306:3306 mysql:8.4` |
| H2 | `h2,spring-data-jpa` | In-memory demo DB with sample data; H2 console at `/petclinic/h2-console`. |
| HSQLDB | `hsqldb,spring-data-jpa` | Alternative in-memory option. |

Schema and data live under `src/main/resources/db/<platform>/`. For PostgreSQL/MySQL you can also start the bundled `docker-compose.yml` with `--profile postgres` or `--profile mysql`.

## Messaging and async workflows
- Appointment events (`AppointmentConfirmedEvent`, `AppointmentVisitLinkedEvent`) are published to RabbitMQ exchange `petclinic.appointments.exchange`.
- Default queues: `appointments.notifications.q` (DLQ: `appointments.notifications.dlq`) and `appointments.availability.q` (DLQ: `appointments.availability.dlq`).
- Internal listeners can be toggled with:
  - `petclinic.messaging.appointments.internal-notifications-consumer-enabled`
  - `petclinic.messaging.appointments.internal-availability-consumer-enabled`
- External worker apps:
  - Notification Service: `./mvnw spring-boot:run -Dspring-boot.run.main-class=org.springframework.samples.petclinic.notifications.NotificationServiceApplication`
  - Scheduling Service: `./mvnw spring-boot:run -Dspring-boot.run.main-class=org.springframework.samples.petclinic.scheduling.SchedulingServiceApplication`

Configure RabbitMQ via `spring.rabbitmq.*` and SMTP via `petclinic.notifications.*`. Datasource settings are read from `SPRING_DATASOURCE_*` (or `application.yml` overrides).

## Testing
- Unit/integration tests: `./mvnw verify`
- Postman regression tests: see `src/test/postman/README.md` (script: `./postman-tests.sh`)
- JMeter performance plan: see `src/test/jmeter/README.md`
- Prometheus endpoint for metrics: `/petclinic/actuator/prometheus`

## Code generation
OpenAPI DTOs and API interfaces are generated into `target/generated-sources` during `mvn clean install` (MapStruct + OpenAPI Generator).

## Contributing
Use the [issue tracker](https://github.com/spring-petclinic/spring-petclinic-rest/issues) for bugs and feature requests. Editor settings live in `.editorconfig`. Angular UI client: https://github.com/spring-petclinic/spring-petclinic-angular.
