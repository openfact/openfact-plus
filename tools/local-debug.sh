#!/usr/bin/env bash

mvn -pl app wildfly-swarm:run -DskipTests -Dswarm.debug.port=5005