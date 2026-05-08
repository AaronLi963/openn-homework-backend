.PHONY: docker-compose

docker-compose:
	docker compose down -v
	docker compose up -d

run-app:
	MYSQL_HOST=localhost \
	MYSQL_PORT=3306 \
	MYSQL_DATABASE=taskdb \
	MYSQL_USER=taskuser \
	MYSQL_PASSWORD=taskpass \
	REDIS_HOST=localhost \
	REDIS_PORT=6379 \
	ROCKETMQ_NAMESRV_ADDR=localhost:9876 \
	./mvnw spring-boot:run

unit-test:
	./mvnw test
