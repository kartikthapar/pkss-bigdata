FROM bdslss_pkss/hadoop:0.1

COPY yarn-site.xml /root/hadoop-2.5.1/etc/hadoop/

RUN mkdir -p /etc/service/resourcemanager
COPY start_resourcemanager.sh /etc/service/resourcemanager/run

EXPOSE 8030 8031 8032 8033 8088 8090
