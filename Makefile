# Docker config.
DOCKER_PATH=./docker/docker-compose.yml
DOCKER_NAME=postgres

# Database
DB_TYPE=postgres
DB_NAME=hawapi

# Colors
RED     := $(shell tput -Txterm setaf 1)
GREEN   := $(shell tput -Txterm setaf 2)
BLUE    := $(shell tput -Txterm setaf 4)
CYAN    := $(shell tput -Txterm setaf 6)
RESET   := $(shell tput -Txterm sgr0)

## Docker

dk-status: ## Check the docker container status.
	@docker ps --filter "name=hawapi-postgres"

dk-run: ## Build & Run the local docker.
	@docker compose -f ${DOCKER_PATH} build ${DOCKER_NAME}
	@docker compose -f ${DOCKER_PATH} up  ${DOCKER_NAME}

dk-start: ## Start the local docker.
	@docker compose -f ${DOCKER_PATH} start ${DOCKER_NAME}
	@sleep 2

dk-stop: ## Stop the local docker.
	@docker compose -f ${DOCKER_PATH} stop ${DOCKER_NAME}

dk-reset: ## Stop, Delete, Build and Start the local docker.
	@docker compose -f ${DOCKER_PATH} stop ${DOCKER_NAME}
	@docker compose -f ${DOCKER_PATH} down --volumes
	@docker compose -f ${DOCKER_PATH} rm ${DOCKER_NAME}
	@docker compose -f ${DOCKER_PATH} build ${DOCKER_NAME}
	@docker compose -f ${DOCKER_PATH} up ${DOCKER_NAME}

dk-prune: ## Delete all docker volumes.
	@docker compose -f ${DOCKER_PATH} rm ${DOCKER_NAME}
	@cd ./docker
	@docker volume prune
	@cd ..

## Help

config: ## Show all configuration (Docker, database, etc...)
	@echo ''
	@echo 'Configuration:'
	@echo '  ${CYAN}Docker:${RESET}'
	@echo '    ${BLUE}Path: ${DOCKER_PATH} ${RESET}'
	@echo '  ${CYAN}Database: ${RESET}'
	@echo '    ${BLUE}Type: ${DB_TYPE} ${RESET}'
	@echo '    ${BLUE}Name: ${DB_NAME} ${RESET}'
	@echo ''

# https://gist.github.com/thomaspoignant/5b72d579bd5f311904d973652180c705
help: ## Show this help.
	@echo ''
	@echo 'Usage:'
	@echo '  ${CYAN}make${RESET} ${GREEN}<target>${RESET}'
	@echo ''
	@echo 'Targets:'
	@awk 'BEGIN {FS = ":.*?## "} { \
		if (/^[a-zA-Z_-]+:.*?##.*$$/) {printf "    ${CYAN}%-20s${GREEN}%s${RESET}\n", $$1, $$2} \
		else if (/^## .*$$/) {printf "  ${BLUE}%s${RESET}\n", substr($$1,4)} \
		}' $(MAKEFILE_LIST)