#!/bin/bash

SHUTDOWN_PIN="3"
echo "$SHUTDOWN_PIN" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio"$SHUTDOWN_PIN"/direction

while ( true )
do
# check if the pin is connected to GND and, if so, halt the system
if [ $(</sys/class/gpio/gpio"$SHUTDOWN_PIN"/value) == 0 ]
then
echo "$SHUTDOWN_PIN" > /sys/class/gpio/unexport
killall php
sleep 1
sync
/sbin/shutdown -h now "System halted by a GPIO action"
exit
fi
sleep 0.1
done
touch /root/shutdownpin.exited
