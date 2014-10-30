#! /bin/sh
# What the docs say:
# /root/hadoop-2.5.1/sbin/hadoop-daemon.sh --config /root/hadoop-2.5.1/etc/hadoop/ --script hdfs start namenode
$HADOOP_PREFIX/bin/hdfs --config $HADOOP_CONF_DIR namenode
