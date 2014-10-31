#!/bin/sh
# What the docs say:
# $HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start nodemanager
exec $HADOOP_PREFIX/bin/yarn --config $HADOOP_CONF_DIR nodemanager
