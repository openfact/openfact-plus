#!/usr/bin/env bash

#Compile
cd apps && mvn clean package && cd ..

# Minishift
oc login -u developer -p developer
oc new-project openfact-dev

APISERVER=$(oc version | grep Server | sed -e 's/.*http:\/\///g' -e 's/.*https:\/\///g')
NODE_IP=$(echo "${APISERVER}" | sed -e 's/:.*//g')
#EXPOSER="NodePort"
EXPOSER="Route"

TEMPLATE="apps/packages/target/classes/META-INF/fabric8/openshift.yml"
echo "Using github client ID: ${GOOGLE_OAUTH_CLIENT_ID} and secret: ${GOOGLE_OAUTH_CLIENT_SECRET}"

echo "Connecting to the API Server at: https://${APISERVER}"
echo "Using Node IP ${NODE_IP} and Exposer strategy: ${EXPOSER}"
echo "Using github client ID: ${GOOGLE_OAUTH_CLIENT_ID} and secret: ${GOOGLE_OAUTH_CLIENT_SECRET}"


OPENFACT_ID="${GOOGLE_OAUTH_CLIENT_ID}"
OPENFACT_SECRET="${GOOGLE_OAUTH_CLIENT_SECRET}"

echo "Applying the OPENFACT template ${TEMPLATE}"
oc process -f ${TEMPLATE} -p APISERVER_HOSTPORT=${APISERVER} -p NODE_IP=${NODE_IP} -p EXPOSER=${EXPOSER} -p GOOGLE_OAUTH_CLIENT_SECRET=${OPENFACT_SECRET} -p GOOGLE_OAUTH_CLIENT_ID=${OPENFACT_ID} | oc apply -f -

echo "Please wait while the pods all startup!"
echo
echo "To watch this happening you can type:"
echo "  oc get pod -l provider=keycloak -w"
echo
echo "Or you can watch in the OpenShift console via:"
echo "  minishift console"
echo
echo "Then you should be able the open the keycloak console here:"
echo "  http://`oc get route keycloak --template={{.spec.host}}`/"