#!/bin/bash

DEVICE=$1

if [ $# -eq 0 ]; then
  echo "No arguments provided. Please add device name."
  echo "  Example: set_baudrate.sh /dev/ttyUSB0"
else
  DEVICE=$1
  sudo stty -F $DEVICE 19200 -cstopb -crtscts
  echo "Updated settings of $DEVICE"
  sudo stty -F $DEVICE
fi
