#!/bin/bash

IFS=',' read -ra hosts <<< "fosters-218,fosters-219,fosters-221,fosters-222,fosters-223"
for host in "${hosts[@]}";
do

ssh $host "pkill -9 -f 'Dyardstick.server'"
ssh $host "pkill -9 -f 'IgniteNode'"
ssh $host "pkill -9 -f 'java'"
ssh $host "rm -r dmagda"

sleep 1

scp -rp /home/gridgain/dmagda $host:~/

ssh $host "chmod -R 777 /home/gridgain/dmagda"

if [ "$host" = "fosters-218" ]; then
ssh $host "/home/gridgain/dmagda/rc-restarter/start.sh"
fi

if [ "$host" = "fosters-219" ]; then
ssh $host "/home/gridgain/dmagda/rc-restarter/start.sh"
fi

if [ "$host" = "fosters-221" ]; then
ssh $host "/home/gridgain/dmagda/rc-restarter/start.sh"
fi

if [ "$host" = "fosters-222" ]; then
ssh $host "/home/gridgain/dmagda/rc-client/start.sh"
fi

if [ "$host" = "fosters-223" ]; then
ssh $host "/home/gridgain/dmagda/rc-client/start.sh"
fi
			
done