#!/bin/bash

minikube start --driver=docker

kubectl create configmap selenoid-config --from-file=browsers.json=./nbank-chart/files/browsers.json

helm install nbank ./nbank-chart

#Проброс портов
#kubectl port-forward svc/frontend 3000:80
#kubectl port-forward svc/backend 4111:4111
#kubectl port-forward svc/selenoid 4444:4444
#kubectl port-forward svc/selenoid-ui 8080:8080