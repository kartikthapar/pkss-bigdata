# Project for Big Data, Small Languages, Scalable Systems

In the docker directory, you will find everything needed to deploy our
infrastructure.  There are 4 docker images: one "superclass" that has a basic
system and includes hadoop, and 3 specializations.  There is an image for the
YARN Resource Manager, HDFS Name Node, and the workers.  The images can all be
built and properly named with `./build_docker_images`. The user running the
script needs to either be in the docker group or be root.


The `fits2csv` directory contains the programs that translate the data from
FITS and place it into HDFS.  It can be built with `make`.
