#!/usr/bin/env bash

mvn clean package -DskipTests=true

oc login -u developer -p developer
oc new-project openfact-sync-system
oc project openfact-sync-system

mvn fabric8:deploy -DSSO_REALM=openfact \
    -DSSO_AUTH_SERVER_URL=http://`oc get route keycloak --template={{.spec.host}}`/auth \
    -DSSO_CLIENT_ID=openfact-online-platform \
    -DSSO_CREDENTIALS_SECRET=b7873f80-6ad7-44a5-97cb-bd210789eb41 \
    -DOPENFACT_SMTP_HOST=${SMTP_HOST} \
    -DOPENFACT_SMTP_PORT=${SMTP_PORT} \
    -DOPENFACT_SMTP_FROM=${SMTP_FROM} \
    -DOPENFACT_SMTP_SSL=${SMTP_SSL} \
    -DOPENFACT_SMTP_STARTTLS=${SMTP_STARTTLS} \
    -DOPENFACT_SMTP_AUTH=${SMTP_AUTH}\
    -DOPENFACT_SMTP_USER=$(echo -n ${SMTP_USER} | base64) \
    -DOPENFACT_SMTP_PASSWORD=$(echo -n ${SMTP_PASSWORD} | base64) \
    -Popenshift \
    -DskipTests=true \
    -Dfabric8.debug.enabled=true