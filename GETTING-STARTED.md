# Getting Started

- [Project Structure](#project-structure)
- [Docker](#docker)
- [Setup](#setup)
- [Tests](#tests)

## Project Structure

<!-- tree -d -I '*.properties|*.java|*.class|target' -->

```
.
├── docker
│   └── postgres
│       ├── init
│       └── migration
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

- Git and Github account (If you want [Contributing](CONTRIBUTING.md))
- Text editor or IDE (IntelliJ IDEA, VsCode, Netbeans, Eclipse)
- Docker
- Java 8 (1.8)
- Npm/Yarn
  - [Astro](https://astro.build/) for [website](https://github.com/HawAPI/website) generation
  - [Retype](https://retype.com/) for [docs](https://github.com/HawAPI/website) generation

## Docker

Setup PostgreSQL database with docker.

> See all [Prerequisites](#prerequisites)

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

### Clone the application

> **Note** \
> Alternatively, you could [download all files (Zip)](https://github.com/HawAPI/HawAPI/archive/refs/heads/main.zip)

1. SSH

```
git clone git@github.com:HawAPI/HawAPI.git
```

2. HTTPS

```
git clone https://github.com/HawAPI/HawAPI.git
```

### Init the database (PostgreSQL)

> **Warning** \
> The application will not run if the database is not active. \
> Check out the [#Docker](#docker) section before.

1. Docker CLI

```
docker compose -f ./docker/docker-compose.yml start postgres
```

2. Makefile

```
make dk-start
```

### Init the application

1. Maven

```
./mvnw spring-boot:run
```

2. Makefile

```
make run
```

### Make a request

1. Maven

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
> Both Unit (Repositories) and Integration tests will required docker to work (Testcontainers prerequisite).

### Init the application tests

To run both **Integration** and **Unit** tests

1. Maven

```
./mvnw test
```

2. Makefile

```
make test
```
