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

<!-- tree -d -I '*.properties|*.java|*.class|target|static' -->

<details>
<summary>Click to expand</summary>

```
.
├── docker
│   └── postgres
│       ├── init
│       └── migration
├── docs
├── logs
├── scripts
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── lucasjosino
    │   │           └── hawapi
    │   │               ├── cache
    │   │               │   └── generator
    │   │               ├── configs
    │   │               │   └── security
    │   │               ├── controllers
    │   │               │   ├── advisor
    │   │               │   ├── api
    │   │               │   │   └── v1
    │   │               │   │       └── auth
    │   │               │   ├── interfaces
    │   │               │   └── utils
    │   │               ├── core
    │   │               ├── enums
    │   │               │   ├── auth
    │   │               │   └── specification
    │   │               ├── exceptions
    │   │               │   ├── auth
    │   │               │   └── specification
    │   │               ├── filters
    │   │               │   ├── base
    │   │               │   └── http
    │   │               ├── jwt
    │   │               │   └── validators
    │   │               ├── models
    │   │               │   ├── base
    │   │               │   ├── dto
    │   │               │   │   ├── auth
    │   │               │   │   └── translation
    │   │               │   ├── http
    │   │               │   ├── translations
    │   │               │   └── user
    │   │               ├── properties
    │   │               ├── repositories
    │   │               │   ├── auth
    │   │               │   ├── base
    │   │               │   ├── specification
    │   │               │   └── translation
    │   │               ├── resolvers
    │   │               ├── services
    │   │               │   ├── auth
    │   │               │   ├── base
    │   │               │   ├── impl
    │   │               │   │   └── auth
    │   │               │   └── utils
    │   │               └── validators
    │   │                   └── annotations
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

</details>

## Prerequisites

- Text editor or IDE (**IntelliJ IDEA**, VsCode, Netbeans, Eclipse)
- Terminal (with bash) for [Scripts](../scripts)
- Docker for [Database](../docker/docker-compose.yml)
- Java 8 (1.8) for [Application](../src/main/java/com/lucasjosino/hawapi/HawAPIApplication.java)
- Npm/Yarn
    - [Astro (v2.5.X)](https://astro.build/) for [website](https://github.com/HawAPI/website) generation
    - [Retype (v3.X.X)](https://retype.com/) for [docs](https://github.com/HawAPI/website) generation

### Optional

- Git and GitHub account for [Contributing](CONTRIBUTING.md)

## Docker

Setup PostgreSQL database with docker.

### Usage

```
docker compose -f ./docker/docker-compose.yml build postgres
docker compose -f ./docker/docker-compose.yml up postgres
```

> **Note** \
> The command `make docker-run` is easy to remember.

### Aliases

```make
docker-status: ## Check the docker container status.
docker-run: ## Build & Run the local docker.
docker-start: ## Start the local docker.
docker-stop: ## Stop the local docker.
docker-reset: ## Stop, Delete, Build and Start the local docker.
docker-prune: ## Delete local docker volumes.
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
make docker-start
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
    dev                      Start docker database and run spring application
    run                      Run the spring application
    get                      Get all pom dependencies
    javadoc                  Generate javadoc files
    test                     Run ALL tests of the spring application
    test-unit                Run ONLY unit tests of the spring application
    test-int                 Run ONLY integration tests of the spring application
    compile                  Compile the spring application
    build                    Build website, test and package the spring application
    verify                   Verify the spring application
    clean                    Clear the spring application
  Build
    jar-run                  Run the compiled application (target/hawapi-*.jar)
  Website
    website-build            Build the website
    website-clean            Remove '.hawapi/' and 'resources/static/'
  Docker
    docker-status            Check the docker container status.
    docker-run               Build & Run the local docker.
    docker-start             Start the local docker.
    docker-stop              Stop the local docker.
    docker-reset             Stop, Delete, Build and Start the local docker.
    docker-prune             Delete local docker volumes.
  Help
    test-setup               Setup for tests
    config                   Show all configuration (Docker, database, etc...)
    help                     Show this help.
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
