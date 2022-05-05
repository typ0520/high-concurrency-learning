#!/bin/bash

docker run -itd -v "$(pwd)/build:/root" --name centos-thread-learning typ0520/centos:7.9.2009 > /dev/null 2>&1
docker exec -it $(docker ps | grep centos-thread-learning | head -n 1 | awk '{print $1}') /bin/bash

#cd /root/classes/java/main/; strace java _1_threadbasic._1_HelloWorld