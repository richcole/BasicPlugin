#!/bin/bash

set -e -x

mvn package

scp target/BasicPlugin-0.0.1-SNAPSHOT.jar ec2-user@54.67.20.48:server/plugins

