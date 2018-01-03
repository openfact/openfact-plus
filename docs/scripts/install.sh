#!/usr/bin/env bash

#Compile
cd apps && mvn clean package && cd ..

# Project configuration
oc login -u developer
oc new-project clarksnut-system

APISERVER=$(oc version | grep Server | sed -e 's/.*http:\/\///g' -e 's/.*https:\/\///g')
NODE_IP=$(echo "${APISERVER}" | sed -e 's/:.*//g')
EXPOSER="Route"

TEMPLATE="apps/packages/target/classes/META-INF/fabric8/openshift.yml"
echo "Using google client ID: ${GOOGLE_OAUTH_CLIENT_ID} and secret: ${GOOGLE_OAUTH_CLIENT_SECRET}"

echo "Connecting to the API Server at: https://${APISERVER}"
echo "Using Node IP ${NODE_IP} and Exposer strategy: ${EXPOSER}"
echo "Using github client ID: ${GOOGLE_OAUTH_CLIENT_ID} and secret: ${GOOGLE_OAUTH_CLIENT_SECRET}"

CLARKSNUT_ID="${GOOGLE_OAUTH_CLIENT_ID}"
CLARKSNUT_SECRET="${GOOGLE_OAUTH_CLIENT_SECRET}"

# Elasticsearch configuration
oc login -u system:admin
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default -n $(oc project -q)

# General configuration
oc login -u developer
echo "Applying the CLARKSNUT template ${TEMPLATE}"
oc process -f ${TEMPLATE} -p APISERVER_HOSTPORT=${APISERVER} -p NODE_IP=${NODE_IP} -p EXPOSER=${EXPOSER} -p GOOGLE_OAUTH_CLIENT_SECRET=${CLARKSNUT_SECRET} -p GOOGLE_OAUTH_CLIENT_ID=${CLARKSNUT_ID} | oc apply -f -

# Deploy clarksnut
mvn clean compile
mvn fabric8:deploy \
    -DKEYCLOAK_SERVER_URL=http://`oc get route keycloak --template={{.spec.host}}`/auth \
    -Popenshift \
    -DskipTests=true

echo "Please wait while the pods all startup!"
echo
echo "To watch this happening you can type:"
echo "  oc get pod -l provider=fabric8 -w"
echo
echo "Or you can watch in the OpenShift console via:"
echo "  minishift console"
echo
echo "Then you should be able the open the fabric8 console here:"
echo "  http://`oc get route clarksnut --template={{.spec.host}}`/"
