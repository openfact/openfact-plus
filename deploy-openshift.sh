#!/usr/bin/env bash

mvn clean package -DskipTests=true

oc login -u developer -p developer
oc new-project openfact-sync-system
oc project openfact-sync-system

mvn fabric8:deploy -DSSO_REALM=openfact \
    -DKEYCLOAK_SERVER_URL=http://`oc get route keycloak --template={{.spec.host}}`/auth \
    -Popenshift \
    -DskipTests=true \
    -Dfabric8.debug.enabled=true