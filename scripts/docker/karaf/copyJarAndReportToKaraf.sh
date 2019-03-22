#!/bin/bash
#
# AET
#
# Copyright (C) 2013 Cognifide Limited
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#



CONTAINER_ID=$(docker ps -qf "name=aet_karaf")

for JAR_NAME in *.jar; do
  docker cp "$JAR_NAME" $CONTAINER_ID:/opt/karaf/deploy
done


REPORT_ID=$(docker ps -qf "name=aet_report")
	docker exec $REPORT_ID rm -rf /var/www/html/*
	docker cp report.zip $REPORT_ID:/tmp/
	docker exec $REPORT_ID bash -c "unzip -o /tmp/report.zip -d /var/www/html"
	docker exec $REPORT_ID bash -c "rm /tmp/report.zip"