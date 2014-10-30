#!/bin/sh
# What the docs say:
# $HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs start datanode
/root/hadoop-2.5.1/bin/hdfs --config /root/hadoop-2.5.1/etc/hadoop/ datanode
