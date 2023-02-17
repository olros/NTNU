.DEFAULT_GOAL := help

.PHONY: help
help: ## List available commands
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

db: ## Run MongoDB and MongoDB-Express in Docker-Compose
	docker-compose up -d

install: ## Install the pip-requirements
	pip3 install -r requirements.txt

stop: ## Stop the MongoDB database
	docker-compose stop

down: ## Stop the database and drop all volumes
	docker-compose down -v

setup: ## Create the database-tables
	python main.py setup

insert: ## Insert data to the tables
	python main.py insert

tasks: ## Run all tasks
	python main.py tasks

start: ## Start MongoDB, install requirements, add tables, fill tables and run tasks
	make db
	make install
	make setup
	make insert
	make tasks

