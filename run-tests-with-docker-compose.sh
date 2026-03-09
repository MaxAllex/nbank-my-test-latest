#!/bin/bash

set -e

IMAGE_NAME=nbank-tests
APIBASEURL=http://backend:4111
UIBASEURL=http://frontend:80
SELENOID_URL=http://selenoid:4444
SELENOID_UI_URL=http://selenoid-ui:8080
DB_URL=jdbc:postgresql://postgres:5432/nbank
DB_USERNAME=postgres
DB_PASSWORD=postgres

TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP

# Собираем Docker образ
echo ">>> Сборка тестов запущена"
docker build -t $IMAGE_NAME .

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

cleanup() {
  echo "Остановка окружения"
  docker compose -f infra/docker_compose/docker-compose.yaml down
}

trap cleanup EXIT

echo "Запуск окружения"
docker compose -f infra/docker_compose/docker-compose.yaml up -d

# Запуск Docker контейнера
echo ">>> Тесты запущены"
docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e APIBASEURL="$APIBASEURL" \
  -e UIBASEURL="$UIBASEURL" \
  -e SELENOID_URL="$SELENOID_URL" \
  -e SELENOID_UI_URL="$SELENOID_UI_URL" \
  -e DB_URL="$DB_URL" \
  -e DB_USERNAME="$DB_USERNAME" \
  -e DB_PASSWORD="$DB_PASSWORD" \
  --network nbank-network \
  "$IMAGE_NAME"

echo "Тесты завершены"