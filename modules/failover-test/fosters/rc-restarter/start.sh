#!/bin/bash

java -DIGNITE_QUIET=false -jar /home/gridgain/dmagda/rc-restarter/servers-starter.jar --cfg /home/gridgain/dmagda/rc-restarter/rcg-server.xml --logCfg ignored --minTtl 60000 --maxTtl 120000 --duration 11520000 >> /home/gridgain/dmagda/server_log.txt 2>&1 &