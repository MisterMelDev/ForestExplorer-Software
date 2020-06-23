#include <Adafruit_NeoPixel.h>
#include <SoftwareSerial.h>
#include <TinyGPS++.h>
#include <PWMServo.h>
#include <MPU9250.h>

#define GPS_RX 9 // tx of the module
#define GPS_TX 8 // rx of the module
#define GPS_BAUDRATE 9600
SoftwareSerial gpsSerial(GPS_RX, GPS_TX);
TinyGPSPlus gps;

#define SERVO_PIN 10
#define SERVO_SPEED 10 // max 90
PWMServo cameraServo;

#define VOLTAGE_PIN A1
#define A1 3
#define A2 2
#define B1 4
#define B2 5

#define MAGNET_DECLINATION 2.06
#define COMPASS_INTERVAL 200
MPU9250 mpu;

long maxLocationTime;
bool gpsFault;

void setup() {
  Serial.begin(115200);

  setupLights();

  gpsSerial.begin(GPS_BAUDRATE);
  cameraServo.attach(SERVO_PIN);

  Wire.begin();
  mpu.setMagneticDeclination(MAGNET_DECLINATION);
  loadCalibration();
  mpu.setup();

  pinMode(A1, OUTPUT);
  pinMode(A2, OUTPUT);
  pinMode(B1, OUTPUT);
  pinMode(B2, OUTPUT);

  reset();
}

void setRightMotor(int dir) {
  if(dir == 0) {
    digitalWrite(A1, HIGH);
    digitalWrite(A2, HIGH);
  } else if(dir == 1) {
    digitalWrite(A1, LOW);
    digitalWrite(A2, HIGH);
  } else if(dir == -1) {
    digitalWrite(A1, HIGH);
    digitalWrite(A2, LOW);
  }
}

void setLeftMotor(int dir) {
  if(dir == 0) {
    digitalWrite(B1, HIGH);
    digitalWrite(B2, HIGH);
  } else if(dir == 1) {
    digitalWrite(B1, HIGH);
    digitalWrite(B2, LOW);
  } else if(dir == -1) {
    digitalWrite(B1, LOW);
    digitalWrite(B2, HIGH);
  }
}

void setServo(int dir) {
  if(dir == 0) {
    cameraServo.write(90);
  } else if(dir == 1) {
    cameraServo.write(90 - SERVO_SPEED);
  } else if(dir == -1) {
    cameraServo.write(90 + SERVO_SPEED);
  }
}

void reset() {
  setRightMotor(0);
  setLeftMotor(0);
  setServo(0);
  setHeadlightsEnabled(false);
  setWarningLightsEnabled(false);
  setBrightness(100);
  gpsFault = false;
  resetCommunication();
  Serial.println("RST");
}

void loop() {
  loopLights();

  if(Serial.available() > 0) {
    serialAvailable();
  }
  
  while(gpsSerial.available() > 0) {
    gps.encode(gpsSerial.read());
  }

  loopCommunicationTimers();

  if(gps.location.isUpdated() || gps.satellites.isUpdated()) {
    Serial.print("l");
    Serial.print(gps.location.lat(), 6);
    Serial.print("/");
    Serial.print(gps.location.lng(), 6);
    Serial.print("/");
    Serial.println(gps.satellites.value());

    maxLocationTime = millis() + 5000;
    gpsFault = false;
  }
}
