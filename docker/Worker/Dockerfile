FROM bdslss_pkss/hadoop:0.1

COPY yarn-site.xml /root/hadoop-2.5.1/etc/hadoop/

RUN mkdir -p /hdfs
RUN mkdir -p /yarn/intermediate
RUN mkdir -p /yarn/logs

RUN mkdir -p /etc/service/datanode
COPY start_datanode.sh /etc/service/datanode/run

RUN mkdir -p /etc/service/nodemanager
COPY start_nodemanager.sh /etc/service/nodemanager/run

EXPOSE 8040 8042 34678 13562
EXPOSE 50010 50075 50020
EXPOSE 50919
