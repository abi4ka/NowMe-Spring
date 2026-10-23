# NowMe Spring Server

## Requirements

- Java 25
- PostgreSQL (start it with `docker compose up -d postgres` from the repository root)

## Local Database

The application uses the `local` profile by default and connects to PostgreSQL on `localhost:5435`, database `nowme`, user `nowme`, password `nowme`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5435/nowme}
spring.datasource.username=${DB_USERNAME:nowme}
spring.datasource.password=${DB_PASSWORD:nowme}
```

- Override `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` to use another database.
