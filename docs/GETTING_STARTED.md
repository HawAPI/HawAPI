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
    │   │               │   ├── advisor
    │   │               │   ├── api
    │   │               │   │   └── v1
    │   │               │   │       └── auth
    │   │               │   ├── interfaces
    │   │               │   └── utils
    │   │               ├── core
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
    │   │               │   ├── properties
    │   │               │   ├── translations
    │   │               │   └── user
    │   │               ├── repositories
    │   │               │   ├── auth
    │   │               │   ├── base
    │   │               │   │   └── impl
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
        │               ├── controllers
        │               │   └── api
        │               │       └── v1
        │               │           └── auth
        │               ├── integration
        │               │   └── auth
        │               ├── repositories
        │               │   ├── auth
        │               │   └── translation
        │               ├── services
        │               │   └── impl
        │               │       └── auth
        │               └── utils
        └── resources
```

</details>

## Prerequisites

- Text editor or IDE (**IntelliJ IDEA**, VsCode, Netbeans, Eclipse)
- Terminal (with bash) for [Scripts](../scripts)
- Docker for [Database](../docker/docker-compose.yml)
- Java 8 (1.8) for [Application](../src/main/java/com/lucasjosino/hawapi/HawAPIApplication.java)

The HawAPI project build **WILL NOT** run when:

- **No** Public/Private RSA keys were found
- Database **IS NOT** accepting connections

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
> Check out the [#Docker](#docker) section before continue.

#### Dev/Test Profiles

- Docker CLI

```
docker compose -f ./docker/docker-compose.yml start postgres
```

- Makefile

```
make docker-start
```

#### Prod (Production) Profile

To use the values on a production mode set these arguments before running the jar file:

- [...] -DSPRING_DATASOURCE_USERNAME=<...>
- [...] -DSPRING_DATASOURCE_PASSWORD=<...>
- [...] -DSPRING_DATASOURCE_URL=<...>

> **Note** \
> Alternatively, set all values on the system environment

### Website/Docs

> **Note** \
> This is optional and only required to display the website and docs.

- Command

```
./scripts/get-website.sh --clean-before
```

- Makefile

```
make get-website
```

> To see all options
>
>```
>./scripts/get-website.sh --help
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
curl http://localhost:8080/api/ping
```

The result should be:

```
Pong
```

## RSA (Rivest–Shamir–Adleman)

> “RSA (Rivest–Shamir–Adleman) is a public-key cryptosystem,
> one of the oldest, that is widely used for secure data transmission.”
>
> _[See more](https://en.wikipedia.org/wiki/RSA_(cryptosystem))_

The HawAPI project build will not run without both Private/Public keys

- [How to set up on: Ubuntu](https://www.digitalocean.com/community/tutorials/how-to-set-up-ssh-keys-on-ubuntu-20-04)
- [How to set up on: Windows](https://learn.microsoft.com/en-us/windows-server/administration/openssh/openssh_keymanagement#user-key-generation)
- [How to set up on: Linux/MacOS](https://help.dreamhost.com/hc/en-us/articles/216499537-How-to-configure-passwordless-login-in-Mac-OS-X-and-Linux)

### Location

Different set up methods are required

#### Dev/Test Profiles

By default, two RSA keys (public/private) are located at:

- /src/main/resources/keys/privateRSAKey.pem
- /src/main/resources/keys/publicRSAKey.pem

> **Warning** \
> Don't use this keys on production mode. **See below**

#### Prod (Production) Profile

To use the keys on a production mode set these arguments before running the jar file:

- [...] -DRSA_PRIVATE_KEY=<...>
- [...] -DRSA_PUBLIC_KEY=<...>

> **Note** \
> Alternatively, set both values on the system environment

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
./mvnw -Dtest="!*IntegrationTest" test
```

- Makefile

```
make test-unit
```

### Integration tests

To run **Integration** tests

- Maven

```
./mvnw -Dtest="*IntegrationTest" test
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
    build-wot                Build website without test and package the spring application
    verify                   Verify the spring application
    clean                    Clear the spring application
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
./scripts/get-website.sh --help
```

```
Usage: ./scripts/get-website.sh [option...]

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
