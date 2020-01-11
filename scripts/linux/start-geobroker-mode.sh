#!/bin/bash

# Licensed under MIT license. See LICENSE for details.
# Copyright (c) 2019 Robert Wittek, https://github.com/robo-w

# Change the following parameters according to your system:

LOG_DIRECTORY=/home/pi/kenwood-gps
PATH_TO_CONVERTER=/home/pi/kenwood-gps/kenwood-gps-converter-1.3.1.jar
GEOBROKER_CONFIG_FILE=/home/pi/kenwood-gps/geobroker-config.js

# Script starts here...

if [ $# -eq 0 ]; then
  echo "No arguments provided. Please add device name."
  echo "  Example: start.sh /dev/ttyUSB0"
else

  DEVICE=$1
  DEVICE_NAME="$(rev <<<$DEVICE | cut -d'/' -f1 | rev)"

  if [ -e "$DEVICE" ]; then
    echo "Setting baudrate to 19200 8N1"
    /home/pi/set_baud_rate_8N1.sh $DEVICE

    echo "Starting GPS Parser in Geobroker mode. Check stderr.log for log output."
    unbuffer cat $DEVICE | java -jar $PATH_TO_CONVERTER -g $GEOBROKER_CONFIG_FILE 2>>$LOG_DIRECTORY/stderr_$DEVICE_NAME.log &
  else
    echo "Device $DEVICE does not exist."
  fi

fi
