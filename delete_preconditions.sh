#!/usr/bin/env bash

oc login -u developer -p developer
oc project clarksnut-system

oc delete all -l group=org.clarksnut.apps