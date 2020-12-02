default: run

.PHONY: run

run:
	export $$(cat .env | xargs); \
	mvn exec:java --quiet
