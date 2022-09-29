run:
	docker-compose build server
	docker-compose up server

deploy:
	docker-compose down
	make run

test:
	docker-compose build test
	docker-compose up test

genKey:
	openssl genrsa -out key.pem 3072
	openssl req -new -x509 -key key.pem -out cert.pem -days 365