#include <Adafruit_NeoPixel.h>
#include <SoftwareSerial.h>
#include <TinyGPS++.h>
#include <Servo.h>

#define GPS_RX 9 // tx of the module
#define GPS_TX 8 // rx of the module
#define GPS_BAUDRATE 9600
SoftwareSerial gpsSerial(GPS_RX, GPS_TX);
TinyGPSPlus gps;

#define SERVO_PIN 10
#define SERVO_SPEED 10 // max 90
Servo cameraServo;

#define VOLTAGE_PIN A1
#define A1 3
#define A2 2
#define B1 4
#define B2 5

String receivedData, packet;

long voltageTimer;
long pingSendTimer, pingReceiveTimer;

boolean handshakeCompleted;
long handshakeTimer;

void setup() {
  Serial.begin(115200);

  setupLights();

  gpsSerial.begin(GPS_BAUDRATE);
  cameraServo.attach(SERVO_PIN);

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
  Serial.println("RST");
  setRightMotor(0);
  setLeftMotor(0);
  setServo(0);
  setHeadlightsEnabled(false);
  setWarningLightsEnabled(false);
  setBrightness(100);
  handshakeCompleted = false;
}

void sendVoltage() {
  float vin = analogRead(VOLTAGE_PIN) * (5.0 / 1023);

  Serial.print("vin");
  Serial.println(vin);
  
  float vout = vin * ((1488 + 892) / 892);

  Serial.print("vout");
  Serial.println(vout);

  voltageTimer = millis() + 15000;
}

void loop() {
  loopLights();

  /*if(millis() >= voltageTimer) {
    sendVoltage();
  }*/

  if(!handshakeCompleted && millis() >= handshakeTimer) {
    Serial.println("H");
    handshakeTimer = millis() + 1000;
    return;
  }

  if(handshakeCompleted && millis() >= pingReceiveTimer) {
    reset();
    return;
  }

  if(handshakeCompleted && millis() >= pingSendTimer) {
    Serial.println("p");
    pingSendTimer = millis() + 500;
    return;
  }

  while(gpsSerial.available() > 0) {
    gps.encode(gpsSerial.read());
  }

  if(gps.location.isUpdated() || gps.satellites.isUpdated()) {
    Serial.print("l");
    Serial.print(gps.location.lat());
    Serial.print("/");
    Serial.print(gps.location.lng());
    Serial.print("/");
    Serial.println(gps.satellites.value());
  }
  
  if(Serial.available() > 0) {
    receivedData = Serial.readStringUntil('\n');
    packet = receivedData.substring(0, 1);

    if(!handshakeCompleted) {
      if(packet == "H") {
        handshakeCompleted = true;
        pingReceiveTimer = millis() + 1000;
      }

      return;
    }

    if(packet == "r") { // Set right motor (-1,0,1)
      int dir = receivedData.substring(1).toInt();
      setRightMotor(dir);
      return;
    }

    if(packet == "l") { // Set left motor (-1,0,1)
      int dir = receivedData.substring(1).toInt();
      setLeftMotor(dir);
      return;
    }

    if(packet == "c") { // Set camera servo (-1,0,1)
      int dir = receivedData.substring(1).toInt();
      setServo(dir);
      return;
    }

    if(packet == "h") { // Set headlights (true/false)
      bool enabled = receivedData.substring(1) == "1";
      setHeadlightsEnabled(enabled);
      return;
    }

    if(packet == "w") { // Set warning lights (true/false)
      bool enabled = receivedData.substring(1) == "1";
      setWarningLightsEnabled(enabled);
      return;
    }

    if(packet == "b") { // Set light brightness (0-100)
      int value = receivedData.substring(1).toInt();
      setBrightness(value);
      return;
    }

    if(packet == "p") {
      pingReceiveTimer = millis() + 1000;
      return;
    }
  }
}
