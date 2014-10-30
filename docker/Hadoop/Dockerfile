# This is a comment
FROM phusion/baseimage:0.9.15

# MAINTAINER Kate Smith <ksmith@example.com>
# RUN apt-get update && apt-get install -y ruby ruby-dev

# Locale setup
RUN apt-get update && apt-get dist-upgrade -y && apt-get install -y \
	language-pack-en-base wget default-jre-headless
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8
ENV LC_ALL en_US.UTF-8

ENV JAVA_HOME /usr/lib/jvm/default-java

WORKDIR /root
RUN wget http://www.motorlogy.com/apache/hadoop/common/stable/hadoop-2.5.1.tar.gz
RUN tar xf hadoop-2.5.1.tar.gz

ENV HADOOP_PREFIX /root/hadoop-2.5.1
ENV HADOOP_CONF_DIR $HADOOP_PREFIX/etc/hadoop/
