#! /bin/sh
javac \
	-classpath .:/root/hadoop-2.5.1/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.5.1.jar:/root/hadoop-2.5.1/share/hadoop/common/hadoop-common-2.5.1.jar:/root/hadoop-2.5.1/share/hadoop/mapreduce/lib/hadoop-annotations-2.5.1.jar \
	-Xlint:deprecation \
	*.java

jar cf PKSS.jar *.class
