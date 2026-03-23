# GAIA-F-Core

GAIA-F is a Genetic Artificial Intelligence Algorithm Framework.

## Prerequisites

- **Java 25** — required to build and run the backend
- **Docker and Docker Compose** — required to run the application

> Gradle and Node.js/npm are managed automatically by the build system and do not need to be installed manually.

## Building

A single command builds both the backend JAR and the frontend static assets:

```bash
./gradlew build
```

This produces:
- `build/libs/gaia-f-core-0.0.1-SNAPSHOT.jar` — the Spring Boot application
- `frontend/dist/` — the compiled React frontend

## Running with Docker

Docker Compose is the standard way to run the application in all environments.

```bash
# Copy the example environment file (adjust ports if needed)
cp .env.example .env

# Build images and start containers
docker compose up --build
```

Once running:
- **Frontend**: http://localhost:3000
- **API**: http://localhost:3000/gaia-f/

### Port Configuration

Host ports are configurable via the `.env` file. See `.env.example` for available options:

| Variable             | Default | Description              |
|----------------------|---------|--------------------------|
| `FRONTEND_HOST_PORT` | `3000`  | Port for the Nginx frontend container |
| `BACKEND_HOST_PORT`  | `8080`  | Port for the Spring Boot backend container |

## Running Tests

```bash
./gradlew test
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
