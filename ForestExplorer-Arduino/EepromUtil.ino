#include <EEPROM.h>

enum EEP_ADDR {
  EEP_CALIB_FLAG = 0x00,
  EEP_ACC_BIAS = 0x01,
  EEP_GYRO_BIAS = 0x0D,
  EEP_MAG_BIAS = 0x19,
  EEP_MAG_SCALE = 0x25
};

void loadCalibration() {
  if(!isCalibrated()) {
    Serial.println("MSGThe MPU9250 is not calibrated. Calibrate for proper results.");
    return;
  }
  
  mpu.setAccBias(0, readFloat(EEP_ACC_BIAS + 0));
  mpu.setAccBias(1, readFloat(EEP_ACC_BIAS + 4));
  mpu.setAccBias(2, readFloat(EEP_ACC_BIAS + 8));
  mpu.setGyroBias(0, readFloat(EEP_GYRO_BIAS + 0));
  mpu.setGyroBias(1, readFloat(EEP_GYRO_BIAS + 4));
  mpu.setGyroBias(2, readFloat(EEP_GYRO_BIAS + 8));
  mpu.setMagBias(0, readFloat(EEP_MAG_BIAS + 0));
  mpu.setMagBias(1, readFloat(EEP_MAG_BIAS + 4));
  mpu.setMagBias(2, readFloat(EEP_MAG_BIAS + 8));
  mpu.setMagScale(0, readFloat(EEP_MAG_SCALE + 0));
  mpu.setMagScale(1, readFloat(EEP_MAG_SCALE + 4));
  mpu.setMagScale(2, readFloat(EEP_MAG_SCALE + 8));
}

bool isCalibrated() {
  return (readByte(EEP_CALIB_FLAG) == 0x01);
}

byte readByte(int address) {
  byte valueIn;
  EEPROM.get(address, valueIn);
  return valueIn;
}

float readFloat(int address) {
  float valueIn;
  EEPROM.get(address, valueIn);
  return valueIn;
}
