#!/bin/bash

docker-compose down
docker-compose build --no-cache
docker compose -f docker-compose-loki.yml -f docker-compose.yml up -d
