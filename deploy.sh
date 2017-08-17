#!/usr/bin/env bash

mvn clean package -DskipTests=true

oc login -u developer -p developer
oc new-project openfact-dev
oc project openfact-dev

mvn fabric8:deploy -DSSO_REALM=openfact \
    -DSSO_AUTH_SERVER_URL=http://`oc get route keycloak --template={{.spec.host}}`/auth \
    -DSSO_CLIENT_ID=openfact-online-platform \
    -DSSO_CREDENTIALS_SECRET=b7873f80-6ad7-44a5-97cb-bd210789eb41 \
    -DES_SERVER_URL=http://192.168.1.30:9200 \
    -DES_USERNAME=elastic \
    -DES_PASSWORD=changeme \
    -Popenshift \
    -DskipTests=true \
    -Dfabric8.debug.enabled=true