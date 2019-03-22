#!/bin/bash

CONTAINER_ID=$(docker ps -qf "name=aet_karaf")

for JAR_NAME in *.jar; do
  docker cp "$JAR_NAME" $CONTAINER_ID:/opt/karaf/deploy
done


REPORT_ID=$(docker ps -qf "name=aet_report")
	docker exec $REPORT_ID rm -rf /var/www/html/*
	docker cp report.zip $REPORT_ID:/tmp/
	docker exec $REPORT_ID bash -c "unzip -o /tmp/report.zip -d /var/www/html"
	docker exec $REPORT_ID bash -c "rm /tmp/report.zip"