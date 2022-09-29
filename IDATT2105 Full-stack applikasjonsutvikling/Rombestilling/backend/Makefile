help:
	@echo 'db                     - start the db and phpmyadmin'
	@echo 'restart                - rebuild and start the db and phpmyadmin'
	@echo 'deploy                 - build dockerimage and deploy it to a Azure Container Registry'
	@echo 'run                    - run backend and db in docker-compose'

db:
	docker-compose up

format:
	./gradlew ktlinFormat --warning-mode=all

restart:
	docker-compose build
	docker-compose up

deploy:
	docker login rombestilling.azurecr.io
	gradlew jib --image=rombestilling.azurecr.io/rombestilling:latest

PR:
	git add .

run-windows:
	gradlew jibDockerBuild --image=rombestilling.azurecr.io/rombestilling:latest
	make run

run-unix:
	./gradlew jibDockerBuild --image=rombestilling.azurecr.io/rombestilling:latest
	make run

run:
	docker-compose -f docker-compose.azure.yml up --build

test:
	./gradlew test
