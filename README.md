# Project for Big Data, Small Languages, Scalable Systems

## Deploying YARN & HDFS
In the docker directory, you will find everything needed to deploy our
infrastructure.  There are 4 docker images: one "superclass" that has a basic
system and includes hadoop, and 3 specializations.  There is an image for the
YARN Resource Manager, HDFS Name Node, and the workers.  The images can all be
built and properly named with `./build_docker_images`. The user running the
script needs to either be in the docker group or be root.

Once the containers are built, spawn exactly one of the resource_manager and
name_node images, and as many of the workers as desired.  The resource_manager
should be run on qp-hd10, the name node on qp-hd12, and workers on qp-hd15 and
qp-hd16

To start a container, run:
```
docker run -d --name=$NAME --net=host $IMAGE
```
In particular:
```
docker run -d --name=resource_manager --net=host bdslss_pkss/resource_manager:0.1
docker run -d --name=name_node --net=host bdslss_pkss/name_node:0.1
docker run -d --name=worker_1 --net=host bdslss_pkss/worker:0.1
...
```


## Dataset stuff

The `fits2csv` directory contains the programs that translate the data from
FITS and place it into HDFS.  It can be built with `make`.
