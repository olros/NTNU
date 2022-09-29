help:
	@echo 'db                     - start the db and phpmyadmin'
	@echo 'restart                - rebuild and start the db and phpmyadmin'
	@echo 'deploy                 - build dockerimage and deploy it to a Azure Container Registry'
	@echo 'test                   - run tests'
	@echo 'PR                    	- run all tests and add to git'
	@echo 'run                    - run backend and db in docker-compose'

db:
	docker-compose up

restart:
	docker-compose build
	docker-compose up

build:
	mvn compile

deploy:
	docker login ${registry}.azurecr.io -u ${registry} -p ${password}
	docker build -t ${registry}.azurecr.io/gidd:latest .
	docker push ${registry}.azurecr.io/gidd:latest

test:
	mvn test --activate-profiles test -e

PR:
	git add .

run:
	docker-compose -f docker-compose.azure.yml build
	docker-compose -f docker-compose.azure.yml up