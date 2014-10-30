FROM bdslss_pkss/hadoop:0.1

COPY hdfs-site.xml /root/hadoop-2.5.1/etc/hadoop/
RUN mkdir -p /etc/service/namenode
COPY start_namenode.sh /etc/service/namenode/

RUN mkdir /hdfs_ns_logs
RUN /root/hadoop-2.5.1/bin/hdfs namenode -format pkss-hdfs
