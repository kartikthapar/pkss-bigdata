FROM bdslss_pkss/hadoop:0.1

COPY yarn-site.xml /root/hadoop-2.5.1/etc/hadoop/

RUN mkdir -p /hdfs
RUN mkdir -p /yarn/intermediate
RUN mkdir -p /yarn/logs

RUN mkdir -p /etc/service/datanode
COPY start_datanode.sh /etc/service/datanode/run

RUN mkdir -p /etc/service/nodemanager
COPY start_nodemanager.sh /etc/service/nodemanager/run
