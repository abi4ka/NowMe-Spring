# NowMe Spring

[![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![JWT](https://img.shields.io/badge/JWT-java--jwt%204.4.0-000000?logo=jsonwebtokens&logoColor=white)](https://github.com/auth0/java-jwt)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-springdoc%202.8.5-85EA2D?logo=swagger&logoColor=black)](https://springdoc.org)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![Gradle](https://img.shields.io/badge/Gradle-9.3.1-02303A?logo=gradle&logoColor=white)](https://gradle.org)

Backend for **NowMe**, a daily photo-sharing app inspired by BeReal: post one photo a day, keep a calendar of your own life and see what your friends are up to.

The Android client lives in [NowMe-Android](https://github.com/abi4ka/NowMe-Android).

## Quick Start

You need JDK 25 and Docker.

1. Create `.env` in the repository root with a JWT signing secret of at least 32 characters. The server refuses to start without it.

   ```bash
   cp .env.example .env
   sed -i "s|^JWT_SECRET=.*|JWT_SECRET=$(openssl rand -base64 48)|" .env
   ```

2. Start PostgreSQL:

   ```bash
   docker compose up -d postgres
   ```

3. Run the server, either from the IDE or with Gradle:

   ```bash
   cd nowme
   ./gradlew bootRun
   ```

The API is served at `http://localhost:25565/`. Swagger UI is at `/swagger-ui.html`, and the OpenAPI JSON is at `/api-docs`.

To run the server in Docker as well, use `docker compose up --build`.

## Configuration

Settings come from environment variables or from `.env`.

| Variable | Default | Purpose |
|---|---|---|
| `JWT_SECRET` | — (required) | HMAC secret for signing tokens, 32+ characters |
| `DB_URL` | `jdbc:postgresql://localhost:5435/nowme` | Database URL |
| `DB_USERNAME` / `DB_PASSWORD` | `nowme` / `nowme` | Database credentials |
| `SEED_ENABLED` | `true` | Fill an empty database with demo data |
| `SEED_RESET` | `false` | Wipe **all** data and seed again on startup |
| `SHOW_SQL` | `false` | Log SQL statements |

PostgreSQL from `docker-compose.yml` listens only on `127.0.0.1:5435`. The schema is managed by Hibernate (`ddl-auto=update`). Uploaded images are stored in `uploads/` next to the working directory.

## Demo Data

On startup the server fills an **empty** database with 13 linked accounts. The data includes 200+ posts with real photos, follows, friendships, likes and comments. Dates are relative to the moment of seeding.

The main account has the most data: 4 months of calendar history, a 35-day streak, 6 pinned posts, friends and followers.

- `nowme_star` / `Star2026!`

All other accounts use the password `password123`:

| Account | Relation to `nowme_star` |
|---|---|
| `anna_k`, `leo.travels`, `mia_cooks`, `sam_runs`, `zoe.art` | friends (mutual follow) |
| `ben_photo`, `kate_music` | followed by `nowme_star`, don't follow back; `ben_photo` has friends-only posts that must stay hidden |
| `fan_tom`, `lucy_reads`, `olga_dev` | follow `nowme_star` |
| `newbie_joe` | no posts and no follows, for checking empty states |
| `ghost_user` | deactivated, must not appear anywhere |

The feed only shows the last 7 days. When demo data gets old, restart once with `SEED_RESET=true`.

Photo sources and licenses are listed in `nowme/src/main/resources/seed/photos/SOURCES.md`.

## How It Works

- **Auth.** Logging in returns an access token, valid for 2 hours, and a refresh token, valid for 28 days. Each refresh rotates the refresh token. Protected endpoints expect `Authorization: Bearer <access token>`.
- **Feed.** Shows posts from the last 7 days by you and the people you follow. Posts marked `FRIENDS_ONLY` are visible to mutual followers only.
- **Uploads.** JPEG, PNG and WebP up to 15 MB are accepted. The type is checked from the file contents, and the server generates the file name.
- **Errors.** Errors are returned as `{"error": "CODE"}` with a matching status: 400 for validation, 401 for auth, 403, 404, 409 for duplicates and 413 for files that are too large.

## API

| Area | Endpoints |
|---|---|
| Auth | `POST /auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout` |
| Posts | `GET /nowme` (feed), `POST /nowme` (multipart), `GET /nowme/{id}/image`, `GET /nowme/me/history`, `GET /nowme/users/{userId}`, `PUT /nowme/{id}/visibility`, `PUT /nowme/{id}/favorite`, `DELETE /nowme/{id}` |
| Likes and comments | `POST`/`DELETE /nowme/{id}/like`, `GET`/`POST /nowme/{id}/comment` |
| Users | `GET /users/me`, `/users/{id}`, `/users/username/{username}`, `/users/search`, `PUT /users/avatar` |
| Follows | `POST`/`DELETE /follow/{userId}` |

List endpoints accept `page` and `size`, with a maximum size of 50. See Swagger UI for request and response details.

## Project Layout

```text
nowme/src/main/java/com/abik/nowme/
  config/            current-user resolver, password encoder, OpenAPI
  module/shared/     auth, JWT, refresh tokens, error handling, paging, demo data seeder
  module/user/       profiles, search, avatars, follows
  module/nowme/      posts, image storage, likes, comments, access rules
```

## Tests

The tests need the PostgreSQL container to be running.

```bash
cd nowme
./gradlew test
```
