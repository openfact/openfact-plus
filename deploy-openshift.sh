#!/usr/bin/env bash

mvn clean package -DskipTests=true

oc login -u developer -p developer
oc new-project openfact-sync-system
oc project openfact-sync-system

mvn fabric8:deploy -DSSO_REALM=openfact \
    -DSSO_AUTH_SERVER_URL=http://`oc get route keycloak --template={{.spec.host}}`/auth \
    -DSSO_CLIENT_ID=openfact-online-platform \
    -DSSO_CREDENTIALS_SECRET=b7873f80-6ad7-44a5-97cb-bd210789eb41 \
    -DSMTP_USER=$(echo -n ${SMTP_USER} | base64) \
    -DSMTP_PASSWORD=$(echo -n ${SMTP_PASSWORD} | base64) \
    -Popenshift \
    -DskipTests=true \
    -Dfabric8.debug.enabled=true