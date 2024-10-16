#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#


echo "Script requires your Kogito Quickstart to be compiled"

PROJECT_VERSION=$(cd ../ && mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

echo "Project version: ${PROJECT_VERSION}"

if [[ $PROJECT_VERSION == *SNAPSHOT ]];
then
  KOGITO_VERSION="main"
else
  KOGITO_VERSION=${PROJECT_VERSION%.*}
fi

echo "Kogito Image version: ${KOGITO_VERSION}"
echo "KOGITO_VERSION=${KOGITO_VERSION}" > ".env"
echo "DOCKER_GATEWAY_HOST=172.17.0.1" >> ".env"

DB="postgresql"

if [ -n "$1" ]; then
  if [[ "$1" == "postgresql" || "$1" == "infinispan" ]];
  then
    DB="$1"
  else
   echo "Usage: By default postgresql environments is started if no argument is provided"
   echo "     start POSTGRESQL docker-compose running: ./startServices.sh postgresql or just ./startServices.sh "
   echo "     start INFINISPAN docker-compose running: ./startServices.sh infinispan "
   exit 1
  fi
fi
echo "Have you compiled the project before with the right profile: ../mvn clean install -DskipTests -P$DB"

if [ "$1" == "infinispan" ];
then
  PERSISTENCE_FOLDER=./persistence
  KOGITO_EXAMPLE_PERSISTENCE=../target/classes/META-INF/resources/persistence/protobuf

  rm -rf $PERSISTENCE_FOLDER

  mkdir -p $PERSISTENCE_FOLDER

  if [ -d "$KOGITO_EXAMPLE_PERSISTENCE" ]
  then
    cp $KOGITO_EXAMPLE_PERSISTENCE/*.proto $PERSISTENCE_FOLDER/
  else
    echo "$KOGITO_EXAMPLE_PERSISTENCE does not exist. Have you compiled the project? mvn clean install -DskipTests -P$DB"
    exit 1
  fi
fi

SVG_FOLDER=./svg

KOGITO_EXAMPLE_SVG_FOLDER=../target/classes/META-INF/processSVG

mkdir -p $SVG_FOLDER

if [ -d "$KOGITO_EXAMPLE_SVG_FOLDER" ]
then
    cp $KOGITO_EXAMPLE_SVG_FOLDER/*.svg $SVG_FOLDER
else
    echo "$KOGITO_EXAMPLE_SVG_FOLDER does not exist. Have you compiled the project?"
    exit 1
fi

docker-compose -f docker-compose-$DB.yml up
