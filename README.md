# NowMe

**NowMe** is a daily photo-sharing application inspired by the real app BeReal. The idea is to motivate users to take a photo every day, so they can follow the story of their own life, keep warm memories, and also see everyday moments from their friends' lives.

The project was developed jointly by two students as their final project. It is split into two connected parts: an Android application for the user experience and a Spring backend for authentication, data storage, images, and social features.

# NowMe Spring

NowMe Spring is the backend server for this system. It provides the API, authentication, persistence, image handling, and social rules that make the Android client work as a real product instead of a standalone demo.

The server receives photo posts, protects user data with JWT-based authentication, serves images back to the app, and keeps track of profiles, follows, likes, comments, favorites, visibility, and personal history.

## Project Pair

- [**NowMe Android**](https://github.com/abi4ka/NowMe-Android): the mobile client that users interact with.
- [**NowMe Spring**](https://github.com/abi4ka/NowMe-Spring): this backend REST API.

The Android app is configured to call the backend locally at:

```text
http://10.0.2.2:25565/
```

The Spring server itself runs on:

```text
http://localhost:25565/
```

## Features

- User registration and login.
- JWT access tokens.
- Refresh token flow.
- Protected endpoints using the `Authorization: Bearer <token>` header.
- User profile loading by current user, id, or username.
- User search with pagination.
- Avatar update.
- Follow and unfollow actions.
- NowMe post creation with multipart image upload.
- Feed of recent NowMe posts.
- Profile post history.
- Current user's full NowMe history.
- Image serving for posts.
- Likes and unlikes.
- Comment creation and paginated comment loading.
- Favorite toggle.
- Post visibility control.
- Post deletion.
- Global exception handling.
- Validation for request DTOs.

## Tech Stack

- Java 25.
- Spring Boot 4.
- Spring Web.
- Spring Data JPA.
- Hibernate.
- Jakarta Validation.
- JWT with `java-jwt`.
- Lombok.
- SQLite for local development.
- PostgreSQL driver included for database flexibility.
- Gradle Kotlin DSL.

## Main Structure

```text
nowme/src/main/java/com/abik/nowme/
  module/shared/
    controller/     Auth controller and global exception handling
    service/        Auth, JWT, and refresh token services
    entity/         Refresh token entity
    repository/     Refresh token repository
    validation/     Custom validation helpers

  module/user/
    controller/     User and follow endpoints
    service/        Profile, follow, and friendship logic
    entity/         User and follow entities
    repository/     User and follow repositories
    dto/            Auth, profile, search, and avatar DTOs

  module/nowme/
    controller/     Post, image, like, comment, history endpoints
    service/        Post, access, image, like, and comment logic
    entity/         NowMe post, like, and comment entities
    repository/     Post, like, and comment repositories
    dto/            Post, comment, and visibility DTOs
```

## Local Requirements

- JDK 25.
- Gradle wrapper from the project.
- Local SQLite database file `nowme-local.db` in the `nowme/` project root.

The local profile is used by default:

```properties
spring.profiles.default=local
server.port=25565
spring.datasource.url=jdbc:sqlite:./nowme-local.db
```

If the database file is missing, SQLite can create an empty file on first start. The application uses `spring.jpa.hibernate.ddl-auto=update`, so the schema is updated during development.

## Running Locally

From the backend module:

```bash
cd nowme
./gradlew bootRun
```

On Windows:

```powershell
cd nowme
.\gradlew.bat bootRun
```

After startup, the API is available at:

```text
http://localhost:25565/
```

## API Overview

Authentication:

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`

NowMe posts:

- `GET /nowme`
- `POST /nowme`
- `GET /nowme/{id}/image`
- `GET /nowme/me/history`
- `GET /nowme/users/{userId}`
- `PUT /nowme/{id}/visibility`
- `PUT /nowme/{id}/favorite`
- `DELETE /nowme/{id}`

Likes and comments:

- `POST /nowme/{id}/like`
- `DELETE /nowme/{id}/like`
- `GET /nowme/{id}/comment`
- `POST /nowme/{id}/comment`

Users and follows:

- `GET /users/me`
- `GET /users/{id}`
- `GET /users/username/{username}`
- `GET /users/search`
- `PUT /users/avatar`
- `POST /follow/{userId}`
- `DELETE /follow/{userId}`

## Relationship With The Android App

The Android app depends on this backend for every core action:

- login and registration;
- token refresh;
- loading the feed;
- uploading camera photos;
- opening post images;
- liking and commenting;
- following users;
- loading profiles and history.

Because both projects were built together, the DTOs and endpoint names are intentionally close on both sides. This makes the project easier to understand, test, and extend.
