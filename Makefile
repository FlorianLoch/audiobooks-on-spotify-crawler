default: run

.PHONY: run

run:
	export $$(cat .env | xargs); \
	mvn exec:java --quiet

set-env:
	export $$(cat .env | xargs);

run-db-in-container:
	docker container rm MyH2Instance; docker run -p 1521:1521 -p 81:81 -v /var/h2data:/opt/h2-data -e H2_OPTIONS="-ifNotExists" --name=MyH2Instance oscarfonts/h2
