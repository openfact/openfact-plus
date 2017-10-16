#!/usr/bin/env bash

mvn clean package -DskipTests=true

oc login -u developer -p developer
oc new-project openfact-sync-system
oc project openfact-sync-system

mvn fabric8:deploy -DSSO_REALM=openfact \
    -DSSO_AUTH_SERVER_URL=http://`oc get route keycloak --template={{.spec.host}}`/auth \
    -DSSO_CLIENT_ID=openfact-online-platform \
    -DSSO_CREDENTIALS_SECRET=b7873f80-6ad7-44a5-97cb-bd210789eb41 \
    -DDS_DRIVER=h2 \
    -DDS_URL='jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE' \
    -DDS_USERNAME=sa \
    -DDS_PASSWORD=sa \
    -DHB_AUTO=create-drop \
    -DHB_SHOW_SQL=true \
    -DHB_FORMAT_SQL=true \
    -DHB_SECOND_LEVEL_CACHE=true \
    -DES_SERVER_URL=http://elasticsearch:9200 \
    -DES_USERNAME='' \
    -DES_PASSWORD='' \
    -DES_INDEX_SCHEMA_MANAGEMENT_STRATEGY=CREATE \
    -DES_REQUIRED_INDEX_STATUS=yellow \
    -Popenshift \
    -DskipTests=true \
    -Dfabric8.debug.enabled=true