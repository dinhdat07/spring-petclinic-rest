# REST version of Spring PetClinic Sample Application (spring-framework-petclinic extension)

[![Java Build Status](https://github.com/spring-petclinic/spring-petclinic-rest/actions/workflows/maven-build-master.yml/badge.svg)](https://github.com/spring-petclinic/spring-petclinic-rest/actions/workflows/maven-build-master.yml)
[![Docker Build Status](https://github.com/spring-petclinic/spring-petclinic-rest/actions/workflows/docker-build.yml/badge.svg)](https://github.com/spring-petclinic/spring-petclinic-rest/actions/workflows/docker-build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=spring-petclinic_spring-petclinic-rest&metric=alert_status)](https://sonarcloud.io/dashboard?id=spring-petclinic_spring-petclinic-rest)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=spring-petclinic_spring-petclinic-rest&metric=coverage)](https://sonarcloud.io/dashboard?id=spring-petclinic_spring-petclinic-rest)

This backend version of the Spring Petclinic application only provides a REST API. **There is no UI**.
The [spring-petclinic-angular project](https://github.com/spring-petclinic/spring-petclinic-angular) is a Angular front-end application which consumes the REST API.

## Understanding the Spring Petclinic application with a few diagrams

[See the presentation of the Spring Petclinic Framework version](http://fr.slideshare.net/AntoineRey/spring-framework-petclinic-sample-application)

### Petclinic ER Model

![alt petclinic-ermodel](petclinic-ermodel.png)

## Running Petclinic locally
### With Maven command line
```sh
git clone https://github.com/spring-petclinic/spring-petclinic-rest.git
cd spring-petclinic-rest
./mvnw spring-boot:run
```

### With Docker
```sh
docker run -p 9966:9966 springcommunity/spring-petclinic-rest
```

You can then access petclinic here: [http://localhost:9966/petclinic/](http://localhost:9966/petclinic/)

There is an actuator health check route as well:
* [http://localhost:9966/petclinic/actuator/health](http://localhost:9966/petclinic/actuator/health)

## 📖 OpenAPI REST API Documentation
This project provides a RESTful API for managing a veterinary clinic's **owners, pets, veterinarians, visits, and specialties**.

### **Access Swagger UI**
Swagger UI is available at: [http://localhost:9966/petclinic/swagger-ui.html](http://localhost:9966/petclinic/swagger-ui.html).

API documentation (OAS 3.1) is accessible at: [http://localhost:9966/petclinic/v3/api-docs](http://localhost:9966/petclinic/v3/api-docs).


## 📌 API Endpoints Overview

| **Method** | **Endpoint** | **Description** |
|-----------|------------|----------------|
| **Owners** |  |  |
| **GET** | `/api/owners` | Retrieve all pet owners |
| **GET** | `/api/owners/{ownerId}` | Get a pet owner by ID |
| **POST** | `/api/owners` | Add a new pet owner |
| **PUT** | `/api/owners/{ownerId}` | Update an owner’s details |
| **DELETE** | `/api/owners/{ownerId}` | Delete an owner |
| **GET** | `/api/owners/{ownerId}/pets/{petId}` | Get a pet by ID (owner’s pet) |
| **PUT** | `/api/owners/{ownerId}/pets/{petId}` | Update pet details (owner’s pet) |
| **POST** | `/api/owners/{ownerId}/pets` | Add a new pet to an owner |
| **POST** | `/api/owners/{ownerId}/pets/{petId}/visits` | Add a vet visit for a pet |
| **Pets** |  |  |
| **GET** | `/api/pets` | Retrieve all pets |
| **GET** | `/api/pets/{petId}` | Get a pet by ID |
| **PUT** | `/api/pets/{petId}` | Update pet details |
| **DELETE** | `/api/pets/{petId}` | Delete a pet |
| **Vets** |  |  |
| **GET** | `/api/vets` | Retrieve all veterinarians |
| **GET** | `/api/vets/{vetId}` | Get a vet by ID |
| **POST** | `/api/vets` | Add a new vet |
| **PUT** | `/api/vets/{vetId}` | Update vet details |
| **DELETE** | `/api/vets/{vetId}` | Delete a vet |
| **Pet Types** |  |  |
| **GET** | `/api/pettypes` | Retrieve all pet types |
| **GET** | `/api/pettypes/{petTypeId}` | Get a pet type by ID |
| **POST** | `/api/pettypes` | Add a new pet type |
| **PUT** | `/api/pettypes/{petTypeId}` | Update pet type details |
| **DELETE** | `/api/pettypes/{petTypeId}` | Delete a pet type |
| **Specialties** |  |  |
| **GET** | `/api/specialties` | Retrieve all vet specialties |
| **GET** | `/api/specialties/{specialtyId}` | Get a specialty by ID |
| **POST** | `/api/specialties` | Add a new specialty |
| **PUT** | `/api/specialties/{specialtyId}` | Update a specialty |
| **DELETE** | `/api/specialties/{specialtyId}` | Delete a specialty |
| **Visits** |  |  |
| **GET** | `/api/visits` | Retrieve all vet visits |
| **GET** | `/api/visits/{visitId}` | Get a visit by ID |
| **POST** | `/api/visits` | Add a new visit |
| **PUT** | `/api/visits/{visitId}` | Update a visit |
| **DELETE** | `/api/visits/{visitId}` | Delete a visit |
| **Appointments** |  |  |
| **GET** | `/api/appointments` | Retrieve all appointments (admin) |
| **GET** | `/api/appointments/queue?status=PENDING&status=CONFIRMED` | Owner-admin queue filtered by appointment status |
| **GET** | `/api/appointments/{appointmentId}` | Get appointment details by ID |
| **POST** | `/api/appointments` | Create an appointment for any owner/pet |
| **POST** | `/api/appointments/{appointmentId}/confirm` | Triage + confirm, optionally assign a vet and write triage notes |
| **POST** | `/api/appointments/{appointmentId}/visits` | Create a visit from a confirmed appointment (1–1 link + optional vet assignment) |
| **PUT** | `/api/appointments/{appointmentId}` | Update appointment schedule/status |
| **DELETE** | `/api/appointments/{appointmentId}` | Delete or cancel an appointment |
| **Users** |  |  |
| **POST** | `/api/users` | Create a new user |


## Screenshot of the Angular client

See its repository here: https://github.com/spring-petclinic/spring-petclinic-angular

<img width="1427" alt="spring-petclinic-angular2" src="https://cloud.githubusercontent.com/assets/838318/23263243/f4509c4a-f9dd-11e6-951b-69d0ef72d8bd.png">

## In case you find a bug/suggested improvement for Spring Petclinic
Our issue tracker is available here: https://github.com/spring-petclinic/spring-petclinic-rest/issues

## Database configuration

By default, Petclinic uses an **in-memory H2 database**, which is automatically populated with sample data at startup.

### **Supported Databases**

Petclinic supports the following databases:

- **H2 (Default, In-Memory)**
- **HSQLDB (Alternative In-Memory Option)**
- **MySQL (Persistent)**
- **PostgreSQL (Persistent)**

### **Switching Databases**

You can change the database by updating the `spring.profiles.active` property in `application.properties`:

| Database  | Profile Configuration |
|-----------|----------------------|
| **H2** (Default)  | `spring.profiles.active=h2,spring-data-jpa` |
| **HSQLDB** (Alternative In-Memory) | `spring.profiles.active=hsqldb,spring-data-jpa` |
| **MySQL** (Persistent) | `spring.profiles.active=mysql,spring-data-jpa` |
| **PostgreSQL** (Persistent) | `spring.profiles.active=postgres,spring-data-jpa` |

For more details, see the [Spring Boot documentation](https://docs.spring.io/spring-boot/how-to/properties-and-configuration.html#howto.properties-and-configuration.set-active-spring-profiles).

### **Using H2 (Default)**
- No additional setup is required.
- The database schema and sample data are loaded automatically from `src/main/resources/db/h2/`.
- You can access the **H2 Console** to inspect the database.

### **Accessing the H2 Console**
1. **Run the application:**
   ```sh
   mvn spring-boot:run
   ```
2. **Open H2 Console in your browser:**
   - **URL**: http://localhost:9966/petclinic/h2-console
   - **JDBC URL**: `jdbc:h2:mem:petclinic`
   - **Username**: `sa`
   - **Password**: _(leave blank)_

### **Using HSQLDB**
- HSQLDB works similarly to H2 as an **in-memory database**.
- No additional setup is required—schema and sample data are loaded automatically from `src/main/resources/db/hsqldb/`.
- Swtich to **HSQLDB** by modifying `application.properties`:

    ```properties
    spring.profiles.active=hsqldb,spring-data-jpa
    ```

### **Using MySQL**
Modify `application.properties`:

```properties
spring.profiles.active=mysql,spring-data-jpa
```
Start a MySQL database using Docker:
```bash
docker run -e MYSQL_USER=petclinic -e MYSQL_PASSWORD=petclinic -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=petclinic -p 3306:3306 mysql:8.4
```

### **Using PostgreSQL**
Modify application.properties:

```properties
spring.profiles.active=postgres,spring-data-jpa
```
Start a PostgreSQL database using Docker:
```bash
docker run -e POSTGRES_USER=petclinic -e POSTGRES_PASSWORD=petclinic -e POSTGRES_DB=petclinic -p 5432:5432 postgres:16.3
```

Instead of manually running containers, you can also use `docker-compose.yml`:

```sh
docker-compose --profile mysql up
docker-compose --profile postgres up
```

### **Further Documentation**
- [HSQLDB](http://hsqldb.org/doc/2.0/guide/index.html)
- [MySQL](https://github.com/spring-projects/spring-petclinic/blob/main/src/main/resources/db/mysql/petclinic_db_setup_mysql.txt)
- [PostgreSQL](https://github.com/spring-projects/spring-petclinic/blob/main/src/main/resources/db/postgres/petclinic_db_setup_postgres.txt)

## API First Approach

This API is built following some [API First approach principles](https://swagger.io/resources/articles/adopting-an-api-first-approach/).

It is specified through the [OpenAPI](https://oai.github.io/Documentation/).
It is specified in this [file](./src/main/resources/openapi.yml).

Some of the required classes are generated during the build time. 
Here are the generated file types:
* DTOs
* API template interfaces specifying methods to override in the controllers

To see how to get them generated you can read the next chapter. 

## Generated code

Some of the required classes are generated during the build time using maven or any IDE (e.g., IntelliJ Idea or Eclipse).

All of these classes are generated into the ``target/generated-sources`` folder.

Here is a list of the generated packages and the corresponding tooling:

| Package name                                   | Tool             |
|------------------------------------------------|------------------|
| org.springframework.samples.petclinic.mapper   | [MapStruct](https://mapstruct.org/)        |
| org.springframework.samples.petclinic.rest.dto | [OpenAPI Generator maven plugin](https://github.com/OpenAPITools/openapi-generator/) |


To get both, you have to run the following command:

```jshelllanguage
mvn clean install
```

## Security configuration
In its default configuration, Petclinic doesn't have authentication and authorization enabled.

### Basic Authentication
In order to use the basic authentication functionality, turn in on from the `application.properties` file
```properties
petclinic.security.enable=true
```
This will secure all APIs and in order to access them, basic authentication is required.
Apart from authentication, APIs also require authorization. This is done via roles that a user can have.
The existing roles are listed below with the corresponding permissions 

* `OWNER_ADMIN` -> `OwnerController`, `PetController`, `PetTypeController` (`getAllPetTypes` and `getPetType`), `VisitController`, `AppointmentController`
* `VET_ADMIN`   -> `PetTypeController`, `SpecialityController`, `VetController`
* `ADMIN`       -> `UserController`
* `OWNER`       -> self-service endpoints under `/api/me/**` (profile, own pets & visit history, appointment scheduling/cancellation) and read-only access to `PetTypeController`
* `VET`         -> vet self-service endpoints under `/api/vets/me/**` (profile, assigned appointments, visit completion) and read-only access to `PetTypeController`/`SpecialtyController`

There are three default users:

* `admin` / `admin` – full access with all administrative roles
* `owner` / `owner` – standard pet owner with self-service permissions
* `vet` / `vet` – veterinarian account with vet self-service permissions

Owners can also sign themselves up by calling the registration endpoint:

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "jdoe",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "address": "742 Evergreen Terrace",
  "city": "Springfield",
  "telephone": "6085554321"
}
```

Upon successful registration the new owner can authenticate via `/api/auth/login` and interact with the self-service API surface.

In order to add a new user, please make `POST /api/users` request with the following payload:

```json
{
    "username": "secondAdmin",
    "password": "password",
    "enabled": true,
    "roles": [
    	{ "name" : "OWNER_ADMIN" }
    ]
}
```

### Owner self-service API

When authenticated with the `OWNER` role the following endpoints become available:

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/api/me/profile` | Retrieve the logged-in owner's profile, pets, and visit history |
| `GET`  | `/api/me/appointments` | List scheduled appointments for the current owner |
| `POST` | `/api/me/appointments` | Schedule a new appointment for one of your pets |
| `DELETE` | `/api/me/appointments/{appointmentId}` | Cancel a pending or eligible appointment |
| `GET`  | `/api/me/pets/{petId}/visits` | List visit history for a specific owned pet |

> Note: Appointments can be cancelled while they are pending or, if already confirmed, at least 24 hours before the scheduled start time.

### Vet self-service API

When authenticated with the `VET` role the following endpoints become available:

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/api/vets/me/profile` | Retrieve the logged-in vet profile and specialties |
| `GET`  | `/api/vets/me/appointments` | List appointments assigned to the vet |
| `POST` | `/api/vets/me/appointments/{appointmentId}/confirm` | Confirm an assigned appointment |
| `POST` | `/api/vets/me/visits/{visitId}/complete` | Mark a visit as completed (and claim it if unassigned) |

Vets also have read-only access to the `PetTypeController` and `SpecialtyController` so they can review pet types and specialty catalogs.

### Owner-admin appointment workflow

Owner administrators are responsible for triage, vet assignment, and keeping the visit queue healthy:

* `GET /api/appointments/queue?status=...` filters the queue by one or many statuses so back-office users can focus on PENDING, CONFIRMED, etc.
* `POST /api/appointments/{id}/confirm` stores triage notes, assigns a vet (optional), and transitions the appointment to `CONFIRMED` atomically.
* `POST /api/appointments/{id}/visits` creates a visit from a confirmed appointment, links it 1–1, copies the triage reason into the visit description, and can push the vet assignment down to the visit record.

All endpoints require `ROLE_OWNER_ADMIN`, live inside the appointments module, and are exposed via an application service/facade so other modules continue to depend solely on exported APIs.

### Booking events & messaging choice

`AppointmentWorkflowService` emits two lightweight domain events through Spring's `ApplicationEventPublisher`:

* `AppointmentConfirmedEvent` — raised every time an appointment is triaged/confirmed (even when already confirmed but vet assignment or notes change).
* `AppointmentVisitLinkedEvent` — raised after a visit is created from an appointment and the linkage succeeds.

These in-process events keep the workflow Modulith-friendly while an AMQP adapter (`AppointmentEventAmqpAdapter`) fans them out through RabbitMQ. We are standardising on **RabbitMQ** for the booking stream because the use-cases need:

1. Low-latency fan-out (notifications, dashboards, SLA timers) rather than long-lived log retention.
2. Strict ordering per appointment/owner but modest throughput.
3. Dead-letter/retry semantics that match triage workflows (e.g. requeue when downstream enrichment fails).

RabbitMQ's routing keys/queues align with those requirements better than Kafka's partition+compaction model, and the adapter publishes JSON payloads to `petclinic.appointments.exchange` using the configurable routing keys `appointments.confirmed` / `appointments.visit-linked` (override via `petclinic.messaging.appointments.*` properties as needed).

### Appointment messaging topology

The default RabbitMQ layout is ready for queue-based load leveling, competing consumers, and dead-letter queues:

| Purpose | Primary queue | DLQ | Routing keys from exchange |
|---------|---------------|-----|----------------------------|
| Notifications / communications | `appointments.notifications.q` | `appointments.notifications.dlq` | `appointments.confirmed`, `appointments.visit-linked` |
| Availability / scheduling | `appointments.availability.q` | `appointments.availability.dlq` | `appointments.confirmed` |

- Exchange: `petclinic.appointments.exchange` (topic)
- Dead-letter exchange: `petclinic.appointments.dlx` (direct)
- Properties to override any name:  
  `petclinic.messaging.appointments.exchange`,  
  `petclinic.messaging.appointments.dead-letter-exchange`,  
  `petclinic.messaging.appointments.notifications-queue`,  
  `petclinic.messaging.appointments.notifications-dlq`,  
  `petclinic.messaging.appointments.availability-queue`,  
  `petclinic.messaging.appointments.availability-dlq`.

Internal consumers are disabled/enabled independently:

- Notifications listener (`AppointmentNotificationsListener`) → `petclinic.messaging.appointments.internal-notifications-consumer-enabled` (default `true`)
- Availability listener (`AppointmentAvailabilityListener`) → `petclinic.messaging.appointments.internal-availability-consumer-enabled` (default `true`)

Each listener is idempotent, logs its work, and throws `AmqpRejectAndDontRequeueException` on failure so the message is dead-lettered. You can switch the internal consumers off (set the property to `false`) when migrating to an external microservice; the queues keep receiving events so another process can bind using the same names without any code changes inside the appointments module.

The DLQs capture failed deliveries for later inspection. Because the workers use standard RabbitMQ semantics, you can scale horizontally simply by adding more instances (competing consumers) or by plugging in a dedicated microservice that listens to the same queues.

### External Notification & Scheduling services

Two standalone Spring Boot entry points ship with the codebase so you can offload work from the modulith without changing domain logic:

| Service | Main class | Queue consumed | Toggle internal consumer off |
|---------|------------|----------------|------------------------------|
| Notification Service | `org.springframework.samples.petclinic.notifications.NotificationServiceApplication` | `appointments.notifications.q` | `petclinic.messaging.appointments.internal-notifications-consumer-enabled=false` |
| Scheduling Service | `org.springframework.samples.petclinic.scheduling.SchedulingServiceApplication` | `appointments.availability.q` | `petclinic.messaging.appointments.internal-availability-consumer-enabled=false` |

Both services reuse the same event contracts (`appointments.events.*`) and `AppointmentMessagingProperties`, so pointing them at the same broker is as simple as sharing `spring.rabbitmq.*` properties. To run them locally against the default topology:

```sh
./mvnw spring-boot:run -Dspring-boot.run.main-class=org.springframework.samples.petclinic.notifications.NotificationServiceApplication
./mvnw spring-boot:run -Dspring-boot.run.main-class=org.springframework.samples.petclinic.scheduling.SchedulingServiceApplication
```

The scheduling service exposes a basic REST probe (`GET /api/scheduling/vets/{vetId}/capacity`) that reports how many confirmed appointments are currently tracked for a vet, showing how downstream services can build their own read models. Because the listeners throw `AmqpRejectAndDontRequeueException` on errors, the messages fall straight into the DLQs described above, regardless of whether they are processed inside the modulith or by the external microservices.

### Notification Service – Email channel

Set `petclinic.messaging.appointments.internal-notifications-consumer-enabled=false` when the external service handles notifications. Then configure the Notification Service with a mail server and enable the email processor:

```properties
petclinic.notifications.service-enabled=true
petclinic.notifications.email.enabled=true
petclinic.notifications.email.from=notifications@petclinic.test
petclinic.notifications.email.owner-recipient=owner@example.com
petclinic.notifications.email.vet-recipient=vet@example.com
petclinic.notifications.email.subject-confirmed=Appointment #{appointmentId} confirmed
petclinic.notifications.email.subject-visit-linked=Visit #{visitId} linked to appointment #{appointmentId}

spring.mail.host=localhost
spring.mail.port=1025  # e.g. MailHog
spring.mail.username=
spring.mail.password=
```

If `petclinic.notifications.email.enabled=false` (default), the service falls back to the lightweight logging processor. Any exception thrown by the email processor will push the message into `appointments.notifications.dlq`, ensuring failed deliveries can be retried once SMTP is restored.

## Working with Petclinic in Eclipse/STS

### prerequisites
The following items should be installed in your system:
* Maven 3 (https://maven.apache.org/install.html)
* git command line tool (https://help.github.com/articles/set-up-git)
* Eclipse with the m2e plugin (m2e is installed by default when using the STS (http://www.springsource.org/sts) distribution of Eclipse)

Note: when m2e is available, there is an m2 icon in Help -> About dialog.
If m2e is not there, just follow the install process here: http://eclipse.org/m2e/download/
* Eclipse with the [mapstruct plugin](https://mapstruct.org/documentation/ide-support/) installed.

### Steps:

1) In the command line
```sh
git clone https://github.com/spring-petclinic/spring-petclinic-rest.git
```
2) Inside Eclipse
```
File -> Import -> Maven -> Existing Maven project
```

## Looking for something in particular?

| Layer | Source |
|--|--|
| REST API controllers | [REST folder](src/main/java/org/springframework/samples/petclinic/rest) |
| Service | [ClinicServiceImpl.java](src/main/java/org/springframework/samples/petclinic/service/ClinicServiceImpl.java) |
| JDBC | [jdbc folder](src/main/java/org/springframework/samples/petclinic/repository/jdbc) |
| JPA | [jpa folder](src/main/java/org/springframework/samples/petclinic/repository/jpa) |
| Spring Data JPA | [springdatajpa folder](src/main/java/org/springframework/samples/petclinic/repository/springdatajpa) |
| Tests | [AbstractClinicServiceTests.java](src/test/java/org/springframework/samples/petclinic/service/clinicService/AbstractClinicServiceTests.java) |

## Publishing a Docker image

This application uses [Google Jib](https://github.com/GoogleContainerTools/jib) to build an optimized Docker image into the [Docker Hub](https://cloud.docker.com/u/springcommunity/repository/docker/springcommunity/spring-petclinic-rest/) repository.
The [pom.xml](pom.xml) has been configured to publish the image with name: `springcommunity/spring-petclinic-rest`

Command line to run:
```sh
mvn compile jib:build -X -DjibSerialize=true -Djib.to.auth.username=xxx -Djib.to.auth.password=xxxxx
```

## Performance Testing

To benchmark the scalability of the PetClinic REST API, a JMeter test plan is available.

- See the [JMeter Performance Test](src/test/jmeter/README.md) for details.
- Run the test using:
  ```sh
  jmeter -n -t src/test/jmeter/petclinic-jmeter-crud-benchmark.jmx \
  -Jthreads=100 -Jduration=600 -Jops=2000 -Jramp_time=120 \
  -l results/petclinic-test-results.jtl

## API Testing with Postman + Newman

This project contains **non-regression tests** for the Petclinic API, built with **Postman** and executed via **Newman**, with automated **HTML reports** for easy analysis.

- See the [Postman + Newman Test](src/test/postman/README.md) for details.
- You can run the tests with 2 ways:
  I. Giving Execution Permission to the script file:
    ```sh
    chmod +x postman-tests.sh
    ./postman-tests.sh
    ```
  II. Without Permission to the script file:
    ```sh
    zsh postman-tests.sh
    ```
> Note: You can use your currently bash installed. Like: "bash postman-tests.sh"

## Interesting Spring Petclinic forks

The Spring Petclinic master branch in the main [spring-projects](https://github.com/spring-projects/spring-petclinic)
GitHub org is the "canonical" implementation, currently based on Spring Boot and Thymeleaf.

This [spring-petclinic-rest](https://github.com/spring-petclinic/spring-petclinic-rest/) project is one of the [several forks](https://spring-petclinic.github.io/docs/forks.html) 
hosted in a special GitHub org: [spring-petclinic](https://github.com/spring-petclinic).
If you have a special interest in a different technology stack
that could be used to implement the Pet Clinic then please join the community there.

# Contributing

The [issue tracker](https://github.com/spring-petclinic/spring-petclinic-rest/issues) is the preferred channel for bug reports, features requests and submitting pull requests.

For pull requests, editor preferences are available in the [editor config](https://github.com/spring-petclinic/spring-petclinic-rest/blob/master/.editorconfig) for easy use in common text editors. Read more and download plugins at <http://editorconfig.org>.
