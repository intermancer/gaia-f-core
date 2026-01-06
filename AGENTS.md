# Agents Guide - Gaia-F Core

This guide provides essential information for agentic coding agents operating in the Gaia-F Core repository. It covers build commands, code style guidelines, and development conventions.

## Project Structure

- **Backend**: Java 21 Spring Boot application in `/src/main/java`
- **Frontend**: React 19 + TypeScript in `/frontend/src`
- **Tests**: JUnit tests in `/src/test/java`, no existing frontend tests yet
- **Build**: Gradle orchestrates both backend and frontend builds

## Build Commands

### Backend (Gradle)

```bash
# Full build (includes frontend)
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests com.intermancer.gaiaf.core.organism.ChromosomeTest

# Run a single test method
./gradlew test --tests com.intermancer.gaiaf.core.organism.ChromosomeTest.testChromosomeConstruction

# Run Spring Boot application
./gradlew bootRun

# Clean build artifacts
./gradlew clean
```

### Frontend (npm via Gradle)

```bash
# From frontend directory
cd frontend

# Install dependencies
npm install

# Build for production
npm run build

# Type check
tsc -b

# Lint
npm run lint

# Development server
npm run dev

# Preview production build
npm run preview
```

## Java Code Style Guidelines

### Imports
- Use explicit imports (no wildcard imports `*`)
- Organize imports by: standard library, third-party, then project packages
- Remove unused imports

### Formatting
- 4-space indentation (Gradle default)
- Keep lines under 120 characters where practical
- Place braces on same line (Stroustrup style): `if (condition) {`
- One blank line between method definitions

### Naming Conventions
- **Classes**: PascalCase (e.g., `Organism`, `BasicExperimentImpl`)
- **Methods/Variables**: camelCase (e.g., `getChromosomes()`, `experimentId`)
- **Constants**: UPPER_SNAKE_CASE
- **Packages**: lowercase, domain-reverse order (e.g., `com.intermancer.gaiaf.core.organism`)

### Type Hints & Documentation
- All public methods must have JavaDoc comments
- Include `@param`, `@return`, `@throws` tags
- Classes should explain their purpose and responsibilities
- Use `@Override` annotation when implementing interface methods

### Error Handling
- Throw checked exceptions for recoverable errors
- Use `IllegalArgumentException` for invalid arguments with clear messages
- Catch exceptions near the source and provide context
- Never silently swallow exceptions without logging

### Testing
- Test classes end with `Test` suffix
- Use JUnit 5 (Jupiter): `@Test`, assertions from `org.junit.jupiter.api.Assertions`
- Import assertions statically: `import static org.junit.jupiter.api.Assertions.*`
- One assertion per test method where possible, or group related assertions
- Use descriptive test names: `testFeatureWhenConditionThenResult()`

### Spring Boot Conventions
- Use `@Component` for general beans, `@Service` for business logic, `@Repository` for data access
- Use constructor injection with `@Autowired` (avoid field injection)
- Keep Spring Framework imports minimal; prefer core interfaces over implementations

## TypeScript/React Code Style Guidelines

### Imports
- Import React when using JSX: `import React, { useState } from 'react'`
- Organize: React → third-party → local relative imports (./components, etc.)
- Use named imports over default where possible

### Formatting
- 2-space indentation (npm/Vite convention)
- Lines up to 100 characters
- Use semicolons (enforced by ESLint config)
- Single quotes for strings

### Naming Conventions
- **Components**: PascalCase (e.g., `MainContent`, `ExperimentStatusView`)
- **Functions/Variables**: camelCase (e.g., `handleStartExperiment`, `isRunning`)
- **Files**: PascalCase for components (e.g., `MainContent.tsx`), camelCase for utilities

### Type Safety
- Always define component props with interfaces: `interface ComponentProps { prop: Type }`
- Use `React.FC<Props>` for functional components
- Enable strict mode in tsconfig (already configured)
- Never use `any` type; use `unknown` if necessary and narrow the type
- Avoid unused variables and parameters (enforced by tsconfig)

### Error Handling
- Use try-catch in async operations
- For fetch errors, check response.ok before proceeding
- Use type guards: `if (error instanceof Error) { error.message }`
- Provide user-facing error messages in UI state

### Component Structure
- Separate concerns: props interface at top, hooks next, then handlers, then JSX
- Import CSS after component code: `import './Component.css'`
- Use hooks for state management (useState for local, Context for shared state)
- Avoid inline function definitions; extract to named functions for event handlers

## Git Workflow

- Commit messages: clear, imperative mood (e.g., "Add organism mutation support")
- Small, focused commits are preferred for easier review
- Push to feature branches before creating pull requests

## Linting & Type Checking

Frontend linting is configured with:
- ESLint with TypeScript support and React Hooks rules
- TypeScript strict mode with no unused locals/parameters
- Run `npm run lint` to check for issues

Backend: No explicit linter configured; follow Java conventions above.

## Environment

- **Java**: 21 (configured in build.gradle)
- **Node**: 22.12.0 (Gradle downloads automatically)
- **npm**: 10.9.0
- **TypeScript**: ~5.9.3
- **React**: ^19.1.1
