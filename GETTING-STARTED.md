# Getting Started

- [Project Structure](#project-structure)
- [Setup](#setup)
- [Docker](#docker)

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

## Setup

Step by step of how to run the application.

### Clone the application

> **Note** \
> Alternatively, you could use something like [degit](https://github.com/Rich-Harris/degit)

**1. SSH**

```
git clone git@github.com:HawAPI/HawAPI.git
```

**2. HTTPS**

```
git clone https://github.com/HawAPI/HawAPI.git
```

### Init the database (PostgreSQL)

> **Warning** \
> Check out [Docker](#docker) section before.

**1. Docker CLI**

```
docker compose -f ./docker/docker-compose.yml start postgres
```

**1. Makefile**

```
make dk-start
```

### Init the application

```
./mvnw spring-boot:run
```

### Test

```
curl localhost:8080/api/ping
```

The result should be:

```
Pong
```

## Docker

Setup PostgreSQL database with docker.

### Prerequisites

- [Docker](https://www.docker.com/)
  - Install on [Linux](https://docs.docker.com/desktop/install/linux-install/)
  - Install on [Windows](https://docs.docker.com/desktop/install/windows-install/)
  - Install on [Mac](https://docs.docker.com/desktop/install/mac-install/)

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
