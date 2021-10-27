#!/bin/bash

# This script can be used to build the Docker image locally, since `docker-compose build` does not support Buildkit secrets

settings=${MLS_M2_SETTINGS:-~/.m2/settings.xml}
app=api-gateway

amqp_tag=wrkfmdit/mls-amqp:${MLS_TAG:-latest}
db_tag=wrkfmdit/mls-db:${MLS_TAG:-latest}
gateway_tag=wrkfmdit/mls-gateway:${MLS_TAG:-latest}

echo "Running Docker build for AMQP service..."
DOCKER_BUILDKIT=1 docker build -t "$amqp_tag" ./amqp

echo "Running Docker build for DB service..."
DOCKER_BUILDKIT=1 docker build -t "$db_tag" ./db

echo "Running Docker build for ${app} with secrets from '${settings}'..."
DOCKER_BUILDKIT=1 docker build \
    --secret id=m2-settings,src="${settings}" \
    --build-arg APP=$app \
    -t "$gateway_tag" ./gateway
