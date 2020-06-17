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
  * 2x SG90 9G servo - €7,90
* LM2596 buck converter (set to 5V) - €3,00
* 4S 5200mAh Turnigy LiPo - €31,95
* 3-4S LiPo battery voltage tester/alarm - €3,05

Subtotal: €261,65

## Folder structure
* **forestexplorer-common**: Contains code used in the deskstop and mobile applications, like networking code.
* **forestexplorer-desktop**: The application running on desktop to control the robot.
* **forestexplorer-mobile**: The application running on the RPi in the robot.
* **ForestExplorer-Arduino**: The code for the Arduino, which communicates over USB Serial with the RPi.
