#!/bin/bash
#переменные для имени образа, пользователя Docker Hub и тега;
IMAGE_NAME=nbank-tests
USER=alexanax
TAG=latest
#чтение токена (лучше из переменной окружения DOCKERHUB_TOKEN, а не хранить в файле);
source .env
export DOCKERHUB_TOKEN
if [ -z "$DOCKERHUB_TOKEN" ]; then
    echo "Ошибка: Переменная DOCKERHUB_TOKEN не задана!"
    exit 1
fi
#логин в Docker Hub через --password-stdin;
echo "$DOCKERHUB_TOKEN" | docker login -u "$USER" --password-stdin
#тегирование образа в формат <dockerhub-username>/<image-name>:<tag>;
docker tag "$IMAGE_NAME" "$USER/$IMAGE_NAME:$TAG"
#пуш образа в Docker Hub;
docker push "$USER/$IMAGE_NAME:$TAG"
#финальное сообщение с тем, как скачать образ командой docker pull ....
echo "Скачать образ: docker pull $USER/$IMAGE_NAME:$TAG"