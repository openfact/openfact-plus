#!/usr/bin/env bash

mvn clean package -Punit-test -Dtest=SpaceProviderTest#getSpacesByQueryTest