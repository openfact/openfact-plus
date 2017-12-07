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
echo "Now adding the SecurityContextConstraints to elasticsearch service account"
echo '{
  "apiVersion": "v1",
  "kind": "SecurityContextConstraints",
  "metadata": {
    "name": "scc-elasticsearch"
  },
  "allowPrivilegedContainer": true,
  "runAsUser": {
    "type": "RunAsAny"
  },
  "seLinuxContext": {
    "type": "RunAsAny"
  },
  "fsGroup": {
    "type": "RunAsAny"
  },
  "supplementalGroups": {
    "type": "RunAsAny"
  },
  "allowedCapabilities": [
    "IPC_LOCK",
    "SYS_RESOURCE"
  ]
}' | oc create -f -

oc adm policy add-scc-to-user scc-elasticsearch system:serviceaccount:$(oc project -q):default
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default

# General configuration
oc login -u developer
echo "Applying the CLARKSNUT template ${TEMPLATE}"
oc process -f ${TEMPLATE} -p APISERVER_HOSTPORT=${APISERVER} -p NODE_IP=${NODE_IP} -p EXPOSER=${EXPOSER} -p GOOGLE_OAUTH_CLIENT_SECRET=${CLARKSNUT_SECRET} -p GOOGLE_OAUTH_CLIENT_ID=${CLARKSNUT_ID} | oc apply -f -
echo "Please wait while the pods all startup!"