#!/usr/bin/env bash

rm -rf org.clarksnut.*
mvn clean package -DskipTests
java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 target/clarksnut-*-swarm.jar -Dsso.auth.server.url="$CLARKSNUT_SSO_SERVER_URL"

