#!/usr/bin/env bash

mvn fabric8:deploy -Popenshift -DSSO_AUTH_SERVER_URL="$CLARKSNUT_SSO_SERVER_URL"