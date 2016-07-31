#!/bin/bash

set -e -x

mvn package

scp /home/richcole/minigames/BasicPlugin/target/BasicPlugin-0.0.1-SNAPSHOT.jar ec2-user@54.153.90.51:server/plugins

