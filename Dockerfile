FROM ubuntu:14.04
MAINTAINER Yufei Zhao <yufeizhao.0@gmail.com>
RUN sudo apt-get update
RUN sudo apt-get install openjdk-7-jdk -y
RUN sudo apt-get install make -y
ADD RMI-master /RMI_master
