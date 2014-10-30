#!/bin/sh
# What the docs say:
# $HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start resourcemanager
$HADOOP_PREFIX/bin/yarn --config $HADDOP_CONF_DIR resourcemanager
