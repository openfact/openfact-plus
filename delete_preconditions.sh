#!/usr/bin/env bash

oc login -u developer -p developer
oc project openfact-dev

oc delete all -l group=org.clarksnut.apps