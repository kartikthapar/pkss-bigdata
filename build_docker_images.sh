#! /bin/sh

VERSION="0.1"

docker build -t="bdslss-pkss/hadoop:${VERSION}" Hadoop
docker build -t="bdslss-pkss/resource-manager:${VERSION}" ResourceManager
docker build -t="bdslss-pkss/name-node:${VERSION}" NameNode
docker build -t="bdslss-pkss/worker:${VERSION}" Worker
