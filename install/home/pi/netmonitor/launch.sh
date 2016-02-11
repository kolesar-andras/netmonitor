#!/bin/bash

basedir="/mnt/data/netmonitor"

last=`cat "$basedir/last"`
last=$((last + 1))
echo $last > "$basedir/last"
dirname=`printf "%04d" $last`
mkdir -p "$basedir/$dirname"

./log.php --section 0 | gzip > "$basedir/$dirname/0.log.gz" &
./log.php --section 1 | gzip > "$basedir/$dirname/1.log.gz" &
./log.php --section 2 | gzip > "$basedir/$dirname/2.log.gz" &
