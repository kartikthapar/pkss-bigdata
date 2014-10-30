#!/bin/sh
# What the docs say:
# $HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start resourcemanager
/root/hadoop-2.5.1/bin/yarn --config /root/hadoop-2.5.1/etc/hadoop/ resourcemanager
