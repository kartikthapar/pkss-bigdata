#! /bin/sh

VERSION="0.1"

docker build -t="bdslss_pkss/hadoop:${VERSION}" Hadoop
docker build -t="bdslss_pkss/resource_manager:${VERSION}" ResourceManager
docker build -t="bdslss_pkss/name_node:${VERSION}" NameNode
docker build -t="bdslss_pkss/worker:${VERSION}" Worker
