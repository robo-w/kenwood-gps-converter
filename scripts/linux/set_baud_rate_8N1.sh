#!/bin/bash

# Licensed under MIT license. See LICENSE for details.
# Copyright (c) 2019 Robert Wittek, https://github.com/robo-w

if [ $# -eq 0 ]; then
  echo "No arguments provided. Please add device name."
  echo "  Example: set_baudrate.sh /dev/ttyUSB0"
else
  DEVICE=$1
  BAUDRATE=19200
  # Using 8N1 settings with given baud rate
  sudo stty -F $DEVICE $BAUDRATE -cstopb -crtscts
  echo "Updated settings of $DEVICE to 8N1 with baudrate $BAUDRATE"
  sudo stty -F $DEVICE
fi
