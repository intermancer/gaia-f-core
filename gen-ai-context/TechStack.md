# Technology Stack

The technologies and frameworks described in this document are used to build the system.

## Languages

Java 25 is the primary language for this system.

## Frameworks

Spring Boot 4.0.1 is used for general structure and configuration.
Jackson 3 is used for serialization and deserialization. Jackson 3 is the default JSON library for Spring Boot 4. Note that `com.fasterxml.jackson.annotation.*` annotations (`@JsonIgnore`, `@JsonCreator`, `@JsonProperty`, `@JsonTypeInfo`, etc.) are intentionally unchanged from Jackson 2 and remain valid in Jackson 3.

## Project root package

The root package for the project is `com.intermancer.gaiaf.core`.

## Web Server

The root context path for the web server is `\gaia-f`

In the containerized architecture, the Spring Boot server is API-only. Static frontend assets are served by the frontend container, not by Spring Boot's static resource serving.

All output should be formatted using indented, pretty JSON. 

The com.intermancer.gaiaf.core.GaiaFCoreApplication class is the Spring Application class, and it also contains any beans required for global configuration.

### JSON Output

All output is pretty JSON.  Even simple collections, such as List<String>, should be output with indentations and on multiple lines.  The configuration should be included in the GaiaFCoreApplication class, since this is a global configuration.

## User Interface

React is used for the user interface. The UI is located in the `frontend` folder.

In all environments (development and production), the React app is compiled to a static artifact by the Vite build and served by an Nginx container. The Vite dev server is not used once the system is containerized.

## Containerization

Docker is used to package and deploy the application. Docker Compose orchestrates the containers in all environments.

### Architecture

The system uses two containers:

- **`backend`**: A JRE 25 container running the Spring Boot JAR. Serves the API only.
- **`frontend`**: An Nginx container serving the compiled React static assets. Proxies `/api` requests to the `backend` container via Docker Compose internal networking (resolved by service name).

### Build Strategy

The JAR and the static assets are built separately on the host before container images are assembled. Neither source code nor build tooling (Gradle, Node, npm) is present in the final images.

- **`backend` image**: copies the JAR produced by `./gradlew build`
- **`frontend` image**: copies the static files produced by `npm run build` (Vite output directory will be a standalone `dist/` directory, not `src/main/resources/static`)

### Networking

Containers communicate over a Docker Compose-managed internal network. The Nginx proxy resolves the backend by its Compose service name (`backend`).

### Ports

All host port bindings are configurable via environment variables or a `.env` file. Default values:

| Container  | Internal Port | Default Host Port |
|------------|---------------|-------------------|
| `backend`  | 8080          | 8080              |
| `frontend` | 80            | 3000              |