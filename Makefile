# Makefile
.DEFAULT_GOAL := help

# Docker config.
DOCKER_PATH=./docker/docker-compose.yml
DOCKER_NAME=postgres
DOCKER_CONTAINER_NAME=hawapi-postgres
DOCKER_PORT=5432

# Database
DB_TYPE=postgres
DB_NAME=hawapi
DB_VERSION=15.1

# Colors
RED     := $(shell tput -Txterm setaf 1)
GREEN   := $(shell tput -Txterm setaf 2)
BLUE    := $(shell tput -Txterm setaf 4)
CYAN    := $(shell tput -Txterm setaf 6)
RESET   := $(shell tput -Txterm sgr0)

## Application

dev: docker-start ## Start docker database and run spring application
	@./mvnw spring-boot:run -e -Dmaven.test.skip=true -DskipTests

run: clean ## Run the spring application
	@./mvnw spring-boot:run -e -Dmaven.test.skip=true -DskipTests

get: ## Get all pom dependencies
	@./mvnw dependency:resolve-plugins

javadoc: ## Generate javadoc files
	@./mvnw javadoc:javadoc

test: test-setup ## Run ALL tests of the spring application
	@./mvnw test

test-unit: test-setup ## Run ONLY unit tests of the spring application
	@./mvnw -Dtest="*UnitTest" test

test-int: test-setup ## Run ONLY integration tests of the spring application
	@./mvnw -Dtest="*IntTest" test

compile: clean ## Compile the spring application
	@./mvnw compile

build: test ## Build website, test and package the spring application
	@./scripts/build-website.sh --clean-before
	@./mvnw package -Dmaven.test.skip=true -DskipTests -Dspring.profiles.active=prod

verify: clean ## Verify the spring application
	@./mvnw verify

clean: ## Clear the spring application
	@./mvnw clean

## Build

jar-run: ## Run the compiled application (target/hawapi-*.jar)
	@./scripts/run-jar.sh

## Website

website-build: ## Build the website
	@./scripts/build-website.sh --clean-before

website-clean: ## Remove '.hawapi/' and 'resources/static/'
	@./scripts/clean-website.sh --clean-hawapi --clean-static

## Docker

docker-status: ## Check the docker container status.
	@docker ps --filter "name=${DOCKER_CONTAINER_NAME}"

docker-run: ## Build & Run the local docker.
	@docker compose -f ${DOCKER_PATH} build ${DOCKER_NAME}
	@docker compose -f ${DOCKER_PATH} up  ${DOCKER_NAME}

docker-start: ## Start the local docker.
	@docker compose -f ${DOCKER_PATH} start ${DOCKER_NAME}
	@sleep 2

docker-stop: ## Stop the local docker.
	@docker compose -f ${DOCKER_PATH} stop ${DOCKER_NAME}

docker-reset: ## Stop, Delete, Build and Start the local docker.
	@docker compose -f ${DOCKER_PATH} stop ${DOCKER_NAME}
	@docker compose -f ${DOCKER_PATH} rm ${DOCKER_NAME}
	@docker compose -f ${DOCKER_PATH} build ${DOCKER_NAME}
	@docker compose -f ${DOCKER_PATH} up ${DOCKER_NAME}

docker-prune: ## Delete local docker volumes.
	@docker compose -f ${DOCKER_PATH} rm ${DOCKER_NAME}
	@cd ./docker
	@docker volume prune

## Help

test-setup: clean ## Setup for tests
	@./mvnw process-test-resources || true
	@cp --remove-destination ./docker/postgres/init/schema.sql ./target/test-classes/schema.sql

config: ## Show all configuration (Docker, database, etc...)
	@echo
	@echo 'Configuration:'
	@echo '  ${BLUE}Docker:${RESET}'
	@echo '    ${CYAN}Port: ${GREEN}${DOCKER_PORT} ${RESET}'
	@echo '    ${CYAN}Path: ${GREEN}${DOCKER_PATH} ${RESET}'
	@echo '    ${CYAN}Name (Container): ${GREEN}${DOCKER_CONTAINER_NAME} ${RESET}'
	@echo '  ${BLUE}Database: ${RESET}'
	@echo '    ${CYAN}Name: ${GREEN}${DB_NAME} ${RESET}'
	@echo '    ${CYAN}Type: ${GREEN}${DB_TYPE} ${RESET}'
	@echo '    ${CYAN}Version: ${GREEN}${DB_VERSION} ${RESET}'
	@echo

# https://gist.github.com/thomaspoignant/5b72d579bd5f311904d973652180c705
help: ## Show this help.
	@echo
	@echo 'Usage:'
	@echo '  ${CYAN}make${RESET} ${GREEN}<target>${RESET}'
	@echo
	@echo 'Targets:'
	@awk 'BEGIN {FS = ":.*?## "} { \
		if (/[a-zA-Z_\-]+:.*?##.*$$/) {printf "    ${CYAN}%-25s${GREEN}%s${RESET}\n", $$1, $$2} \
		else if (/^## .*$$/) {printf "  ${BLUE}%s${RESET}\n", substr($$1,4)} \
		}' $(MAKEFILE_LIST)