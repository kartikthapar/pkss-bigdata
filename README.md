# Project for Big Data, Small Languages, Scalable Systems

## Deploying YARN & HDFS
In the docker directory, you will find everything needed to deploy our
infrastructure.  There are 4 docker images: one "superclass" that has a basic
system and includes hadoop, and 3 specializations.  There is an image for the
YARN Resource Manager, HDFS Name Node, and the workers.  The images can all be
built and properly named with `./build_docker_images`. The user running the
script needs to either be in the docker group or be root.

Once the containers are built, spawn exactly one of the resource_manager and
name_node images, and as many of the workers as desired.  Obtain the IP
addresses of all the containers (`ip a` on the inside).  On the resource
manager and the workers, append entries into `/etc/hosts` for the Resource
Manager and the HDFS Name Node:
```
172.17.0.2    resource-manager
172.17.0.3    name-node
```
Then restart the resource manager (`sv restart resourcemanager` on the Resource
Manager) and the Node Managers (`sv restart nodemanager` on the workers).  The
NameNode needs to be able to resolve the names of all the data nodes, in
addition to itself and the Resource Manager.  Its `/etc/hosts` should look something like:
```
172.17.0.2    resource-manager
172.17.0.3    name-node
172.17.0.4    1234567890abcdef
172.17.0.5    fedcba0987654321
...
```
Then restart the name node (`sv restart namenode`).

## Dataset stuff

The `fits2csv` directory contains the programs that translate the data from
FITS and place it into HDFS.  It can be built with `make`.
