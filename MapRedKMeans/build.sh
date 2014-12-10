#! /bin/sh
javac \
	-classpath .:${HADOOP_PREFIX}/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.5.1.jar:${HADOOP_PREFIX}/share/hadoop/common/hadoop-common-2.5.1.jar:${HADOOP_PREFIX}/share/hadoop/mapreduce/lib/hadoop-annotations-2.5.1.jar \
	-Xlint:deprecation \
    -Xlint:unchecked \
    edu/jhu/bdslss/pkss/*.java \
    edu/jhu/bdslss/*.java

jar cf PKSS.jar edu/jhu/bdslss/pkss/*.class
