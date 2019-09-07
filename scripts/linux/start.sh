#!/bin/bash

DEVICE_NAME=ttyUSB0
DEVICE=/dev/$DEVICE_NAME

if [ -e "$DEVICE" ]; then
  echo "Setting baudrate to 19200 8N1"
  /home/pi/set_baud_rate_8N1.sh $DEVICE

  echo "Starting GPS Parser in Geobroker mode. Check stderr.log for log output."
  unbuffer cat $DEVICE | java -jar /home/pi/kenwood-gps/kenwood-gps-converter-1.2.1.jar -g /home/pi/kenwood-gps/geobroker-config.js 2>> /home/pi/kenwood-gps/stderr_$DEVICE_NAME.log &
else
  echo "Device $DEVICE does not exist."
fi
