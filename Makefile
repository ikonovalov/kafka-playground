start:
	docker-compose start

stop:
	docker-compose stop

restart:
	docker-compose stop && docker-compose start

ps:
	docker-compose ps

reset:
	docker-compose down & docker-compose up

kafka:
	docker-compose exec kafka1 bash

desc:
	docker-compose exec kafka1 bash -c 'kafka-topics.sh --bootstrap-server `hostname`:9092 --describe  --exclude-internal'

desc-all:
	 exec kafka1 bash -c 'kafka-topics.sh --bootstrap-server `hostname`:9092 --describe'

jars-upload:
	 curl -XPOST -F "jarfile=@target/kafka-showcase-1.0.0.jar" http://localhost:8081/v1/jars/upload
