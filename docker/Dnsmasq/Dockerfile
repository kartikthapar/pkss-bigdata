# This is a comment
FROM phusion/baseimage:0.9.15

# Locale setup
RUN apt-get update && apt-get dist-upgrade -y && apt-get install -y \
	language-pack-en-base wget dnsmasq
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8
ENV LC_ALL en_US.UTF-8

ADD id_dsa.pub /tmp/id_dsa.pub
RUN cat /tmp/id_dsa.pub >> /root/.ssh/authorized_keys && rm -f /tmp/id_dsa.pub

EXPOSE 22
EXPOSE 53

RUN mkdir -p /etc/service/dnsmasq
COPY dnsmasq.conf /etc/dnsmasq.conf
COPY hosts /root/hosts
COPY start_dnsmasq.sh /etc/service/dnsmasq/run
