#!/usr/bin/env bash

mvn clean package -DskipTests=true

oc login -u developer -p developer
oc new-project clarksnut-system
oc project clarksnut-system

mvn clean compile
mvn fabric8:deploy \
    -DKEYCLOAK_SERVER_URL=http://`oc get route keycloak --template={{.spec.host}}`/auth \
    -Popenshift \
    -DskipTests=true
    -Dfabric8.debug.enabled=true