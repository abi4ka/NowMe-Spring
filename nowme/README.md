# NowMe Spring Server

## Requirements

- Java 25
- The local SQLite database file `nowme-local.db`

## Local Database

The application uses the `local` profile by default and expects this database file in the project root:

- `nowme-local.db`

The configured connection is:

```properties
spring.datasource.url=jdbc:sqlite:./nowme-local.db
```

- If `nowme-local.db` is missing, SQLite can create an empty database file on first start.