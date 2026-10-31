# careround-user-service

Staff profiles (doctors, nurses, admins). HTTP 8082, gRPC 9091, DB `careround_user_db`.

- **Kafka in:** `user.registered` (creates the Doctor/Nurse/Admin profile with the same id as the auth account),
  `user.registration_cancelled`.
- **REST:** `GET /api/v1/users/{publicId}`.
- **gRPC server (9091):** `LicenseVerificationService.LicenseNumberExists` (auth-service checks duplicate licence
  numbers), `NurseSeniorityService.GetMostSeniorNurse` (ward-service charge succession).
- Grades are the `DoctorGrade` / `NurseGrade` enums.

## Environment variables

Copy `.env.example` to `.env` (gitignored) and fill in real values. `main()` loads `.env` into system properties;
plain environment variables work too.

| Variable | Purpose |
|---|---|
| `DB_USERNAME` | Postgres user (root docker-compose: careround-postgres) |
| `DB_PASSWORD` | Postgres password |
| `JWT_PUBLIC_KEY` | RSA public key used to verify access tokens; same value in every service. PEM on one line, `|` for newlines |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka (root docker-compose: careround-kafka) |

## Run

Needs the root `docker-compose.yml` stack (Postgres, Kafka, Redis) and a `.env`.

```bash
./mvnw clean package -DskipTests && java -jar target/careround-user-service-0.0.1-SNAPSHOT.jar
```

Clients reach the REST API through the gateway on `http://localhost:8080`; the service port also works directly.
