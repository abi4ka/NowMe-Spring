# NowMe Spring Server

## Requirements

- Java 25
- PostgreSQL (start it with `docker compose up -d postgres` from the repository root)
- A `.env` file in the repository root with `JWT_SECRET` (at least 32 characters, see `.env.example`)

## Local Database

The application uses the `local` profile by default and connects to PostgreSQL on `localhost:5435`, database `nowme`, user `nowme`, password `nowme`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5435/nowme}
spring.datasource.username=${DB_USERNAME:nowme}
spring.datasource.password=${DB_PASSWORD:nowme}
```

- Override `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` to use another database.

## Demo Data

An empty database is filled with demo data on startup. Log in as `nowme_star` / `Star2026!` (other demo accounts use `password123`).
Set `SEED_ENABLED=false` to turn it off, or `SEED_RESET=true` to wipe all data and seed again. See the root `README.md` for details.
