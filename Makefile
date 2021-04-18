# Docker-compose
start:
	docker-compose start
stop:
	docker-compose stop
logs:
	docker-compose logs -f
restart:
	docker-compose stop && docker-compose start
ps:
	docker-compose ps
reset:
	docker-compose down & docker-compose up

# Kafka
kafka-bash:
	docker-compose exec kafka1 bash
topic-desc:
	docker-compose exec kafka1 bash -c 'kafka-topics.sh --bootstrap-server `hostname`:9092 --describe  --exclude-internal'
topic-desc-all:
	docker-compose exec kafka1 bash -c 'kafka-topics.sh --bootstrap-server `hostname`:9092 --describe'
topic-create:
	docker-compose exec kafka1 bash -c 'kafka-topics.sh --bootstrap-server `hostname`:9092 --create --topic=$(name) --partitions 10 --replication-factor 2'
topic-delete:
	docker-compose exec kafka1 bash -c 'kafka-topics.sh --bootstrap-server `hostname`:9092 --delete --topic=$(name)'
topic-default-create:
	docker-compose exec kafka1 bash -c 'kafka-topics.sh --bootstrap-server `hostname`:9092 --create --topic=dev-mpart --partitions 10 --replication-factor 2'
	docker-compose exec kafka2 bash -c 'kafka-topics.sh --bootstrap-server `hostname`:9092 --create --topic=sink-topic --partitions 10 --replication-factor 2'

# Flink
flink-bash:
	docker-compose exec jobmanager1 bash
jar-rebuild:
	mvn clean package
jar-upload:
	 curl -XPOST -F "jarfile=@target/kafka-showcase-1.0.0.jar" http://localhost:8081/v1/jars/upload | jq
jar-list:
	curl -XGET  http://localhost:8081/v1/jars | jq
jar-run:
	curl -XPOST  http://localhost:8081/v1/jars/$(id)/run | jq
