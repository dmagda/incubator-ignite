#!/bin/bash

rm -r dmagda_res/*

IFS=',' read -ra hosts <<< "fosters-218,fosters-219,fosters-221,fosters-222,fosters-223"
for host in "${hosts[@]}";
do

scp -rp  $host:~/dmagda dmagda_res/$host

done





