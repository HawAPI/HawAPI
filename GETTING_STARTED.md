# Getting Started

- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Docker](#docker)
    - [Usage](#usage)
    - [Aliases](#aliases)
- [Setup](#setup)
    - [Clone](#clone)
    - [Database](#database)
    - [Website/Docs](#websitedocs)
    - [Application](#application)
- [Tests](#tests)
    - [Unit Tests](#unit-tests)
    - [Integration Tests](#integration-tests)
- [Scripts](#scripts)
    - [Makefile](#makefile)
    - [Shell/Bash](#shellbash)

## Project Structure

<!-- tree -d -I '*.properties|*.java|*.class|target' -->

```
.
├── docker
│   └── postgres
│       ├── init
│       └── migration
├── scripts
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── lucasjosino
    │   │           └── hawapi
    │   │               ├── configs
    │   │               │   └── security
    │   │               ├── controllers
    │   │               │   └── auth
    │   │               ├── enums
    │   │               │   └── auth
    │   │               ├── exceptions
    │   │               │   └── auth
    │   │               ├── filters
    │   │               │   ├── base
    │   │               │   └── http
    │   │               ├── interfaces
    │   │               ├── jwt
    │   │               │   └── validators
    │   │               ├── models
    │   │               │   ├── base
    │   │               │   └── user
    │   │               ├── properties
    │   │               ├── repositories
    │   │               │   └── auth
    │   │               ├── resolvers
    │   │               └── services
    │   │                   ├── auth
    │   │                   └── utils
    │   └── resources
    │       └── keys
    └── test
        ├── java
        │   └── com
        │       └── lucasjosino
        │           └── hawapi
        │               ├── configs
        │               │   └── initializer
        │               ├── integration
        │               │   └── auth
        │               ├── unit
        │               │   ├── controllers
        │               │   │   └── auth
        │               │   ├── repositories
        │               │   └── services
        │               └── utils
        └── resources
```

## Prerequisites

- Git and GitHub account (If you want [Contributing](CONTRIBUTING.md))
- Text editor or IDE (IntelliJ IDEA, VsCode, Netbeans, Eclipse)
- Docker
- Java 8 (1.8)
- Npm/Yarn
    - [Astro](https://astro.build/) for [website](https://github.com/HawAPI/website) generation
    - [Retype](https://retype.com/) for [docs](https://github.com/HawAPI/website) generation

## Docker

Setup PostgreSQL database with docker.

### Usage

```
docker compose -f ./docker/docker-compose.yml build postgres
docker compose -f ./docker/docker-compose.yml up postgres
```

> **Note** \
> The command `make dk-run` is easy to remember.

### Aliases

```make
dk-status: ## Check the docker container status.
dk-run: ## Build & Run the local docker.
dk-start: ## Start the local docker.
dk-stop: ## Stop the local docker.
dk-reset: ## Stop, Delete, Build and Start the local docker.
dk-prune: ## Delete all docker volumes.
```

## Setup

Step by step of how to run the application.

> See all [Prerequisites](#prerequisites)

### Clone

> **Note** \
> Alternatively, you could [download all files (Zip)](https://github.com/HawAPI/HawAPI/archive/refs/heads/main.zip)

- SSH

```
git clone git@github.com:HawAPI/HawAPI.git
```

- HTTPS

```
git clone https://github.com/HawAPI/HawAPI.git
```

### Database

> **Warning** \
> The application will not run if the database is not active. \
> Check out the [#Docker](#docker) section before.

- Docker CLI

```
docker compose -f ./docker/docker-compose.yml start postgres
```

- Makefile

```
make dk-start
```

### Website/Docs

> **Note** \
> This is optional and only required to display the website and docs.

- Command

```
./scripts/build-website.sh --clean-before
```

- Makefile

```
make build-website
```

> To see all options
>
>```
>./scripts/build-website.sh --help
>```

### Application

- Maven

```
./mvnw spring-boot:run
```

- Makefile

```
make run
```

### Make a request

- Command

```
curl localhost:8080/api/ping
```

The result should be:

```
Pong
```

## Tests

The application include both: Integration and Unit tests

- Unit
    - Controllers
    - Services
    - Repositories
- Integration
    - Controllers (Which will call services and repositories)

> **Note** \
> Both Unit (Repositories) and Integration tests will require docker to work (Testcontainers prerequisite).

### Init the tests

To run both **Integration** and **Unit** tests

- Maven

```
./mvnw test
```

- Makefile

```
make test
```

### Unit tests

To run **Unit** tests

- Maven

```
./mvnw -Dtest="*UnitTest" test
```

- Makefile

```
make test-unit
```

### Integration tests

To run **Integration** tests

- Maven

```
./mvnw -Dtest="*IntTest" test
```

- Makefile

```
make test-int
```

## Scripts

A collection of command line scripts for executing common project commands

### Makefile

```
make help
```

```
Usage:
  make <target>

Targets:
  Application
    run                 Run the spring application
    test                Run ALL tests of the spring application
    test-unit           Run ONLY unit tests of the spring application
    test-int            Run ONLY integration tests of the spring application
    compile             Compile the spring application
    build               Build website, test and package the spring application
    verify              Verify the spring application
    clean               Clear the spring application
  Build
    run-jar             Run the compiled application (target/hawapi-*.jar)
  Website
    build-website       Build the website
    clean-website       Remove '.hawapi/' and 'resources/static/'
  Docker
    dk-status           Check the docker container status.
    dk-run              Build & Run the local docker.
    dk-start            Start the local docker.
    dk-stop             Stop the local docker.
    dk-reset            Stop, Delete, Build and Start the local docker.
    dk-prune            Delete all docker volumes.
  Help
    test-setup          Setup for tests
    config              Show all configuration (Docker, database, etc...)
    help                Show this help.

```

### Shell/Bash

#### Build Website

```
./scripts/build-website.sh --help
```

```
Usage: ./scripts/build-website.sh [option...]

   -B, --clean-before           Remove '.hawapi/' directory before building the website
   -H, --clean-hawapi           Remove '.hawapi/' directory
   -D, --clean-downloads        Remove '.downloads/' directory
   -S, --clean-static           Remove 'resources/static/' directory
   -A, --clean-all              Remove all directories related with website build
```

#### Clean Website

```
./scripts/clean-website.sh --help
```

```
Usage: ./scripts/clean-website.sh [option...]

   -H, --clean-hawapi           Remove '.hawapi/' directory
   -D, --clean-downloads        Remove '.downloads/' directory
   -S, --clean-static           Remove 'resources/static/' directory
   -A, --clean-all              Remove all directories related with website build
```

#### Run Jar

```
./scripts/run-jar.sh --help
```

```
Usage: ./scripts/run-jar.sh [option...]

   -S, --skip-tests         Skip tests when building the application
   -N, --no-clean           Don't remove 'target/' directory before building
```
