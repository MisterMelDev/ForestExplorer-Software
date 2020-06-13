# ForestExplorer-Software
This repository contains all code for the ForestExplorer project.

## Hardware
* DFRobot Devestator robot frame - €99,95
* Raspberry Pi 3B+ with: - €39,95
  * Logitech C270 webcam - €29,95
  * USB connection to Arduino - €0,50
  * 32GB SD card - €6
* Arduino Nano with: - €5,90
  * Cytron MDD3A motor driver - €7,20
  * ACS711EX -15.5A to 15.5A current sensor - €3,95
  * 18x WS2812B LEDs - €10,00
  * GY-NEO6MV2 GPS module - €14,30
  * MPU9250 accelerometer/gyroscope/magnetometer module - €9,00
* LM2596 buck converter (set to 5V) - €3,00
* 4S 5200mAh Turnigy LiPo - €31,95

Subtotal: €261,65

## Folder structure
`forestexplorer-common` contains code used in both `forestexplorer-desktop` and `forestexplorer-mobile`, like packets.  
`forestexplorer-desktop` is the program run on desktop to control the robot.  
`forestexplorer-mobile` is the program running on the RPi.  
`NewRover-Arduino` contains the code in the Arduino controller, which communicates over USB with the RPi.
