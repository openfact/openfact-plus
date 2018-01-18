#!/usr/bin/env bash

oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default -n $(oc project -q)

mvn fabric8:deploy -Popenshift -DskipTests=true