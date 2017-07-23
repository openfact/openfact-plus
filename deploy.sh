#!/usr/bin/env bash

oc login -u developer -p developer
oc project openfact-dev

mvn clean package -DskipTests=true

mvn fabric8:deploy -DSSO_REALM=openfact \
    -DSSO_AUTH_SERVER_URL=http://`oc get route keycloak --template={{.spec.host}}`/auth \
    -DSSO_CLIENT_ID=openfact-online-platform \
    -DSSO_CREDENTIALS_SECRET=b7873f80-6ad7-44a5-97cb-bd210789eb41 \
    -Popenshift \
    -DskipTests=true \
    -Dfabric8.debug.enabled=true