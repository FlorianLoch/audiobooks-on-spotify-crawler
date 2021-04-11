default: run

.PHONY: compile run set-env run-db-in-container

compile:
	mvn compile

run:
	export $$(cat .env | xargs); \
	mvn exec:java --quiet -Dexec.args="--clean-run"

set-env:
	export $$(cat .env | xargs);

run-db-in-container:
	docker container rm --force h2db; docker run -p 127.0.0.1:1521:1521 -p 127.0.0.1:81:81 -v /var/h2data:/opt/h2-data -e H2_OPTIONS="-ifNotExists" --name=h2db oscarfonts/h2:alpine
