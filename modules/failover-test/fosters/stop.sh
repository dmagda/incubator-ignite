#!/bin/bash

IFS=',' read -ra hosts <<< "fosters-218,fosters-219,fosters-221,fosters-222,fosters-223"
for host in "${hosts[@]}";
do

ssh $host "pkill -9 -f 'Dyardstick.server'"
ssh $host "pkill -9 -f 'IgniteNode'"
ssh $host "pkill -9 -f 'java'"

sleep 1

done