reset:
	docker-compose down & docker-compose up

kafka:
	docker-compose exec kafka1 bash

desc:
	docker-compose exec kafka1 bash -c 'kafka-topics.sh --bootstrap-server `hostname`:9092 --describe  --exclude-internal'

desc-all:
	 exec kafka1 bash -c 'kafka-topics.sh --bootstrap-server `hostname`:9092 --describe'