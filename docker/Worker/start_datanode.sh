#!/bin/sh
# What the docs say:
# $HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs start datanode
$HADOOP_PREFIX/bin/hdfs --config $HADOOP_CONF_DIR datanode
