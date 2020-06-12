#include <Adafruit_NeoPixel.h>
#include <SoftwareSerial.h>
#include <TinyGPS++.h>
#include <Servo.h>

#define LED_PIN 7
#define LED_NUM 18
#define HEADLIGHT_NUM 16
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(LED_NUM, LED_PIN, NEO_GRB + NEO_KHZ800);

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

int brightness = 100;
boolean warningLights, warningLightsState;
long warningLightsTimer;
boolean headlights;

long voltageTimer;
long pingSendTimer, pingReceiveTimer;

boolean handshakeCompleted;
long handshakeTimer;

void setup() {
  Serial.begin(115200);

  pixels.begin();
  pixels.show();

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
  setHeadlights(false);
  setWarningLights(false);
  brightness = 100;
  handshakeCompleted = false;
}

void setHeadlights(bool enabled) {
  if(enabled) {
    int fullBrightness = getBrightness(255);
    for(int i = 0; i < HEADLIGHT_NUM; i++) {
      pixels.setPixelColor(i, pixels.Color(fullBrightness, fullBrightness, fullBrightness));
    }
  } else {
    for(int i = 0; i < HEADLIGHT_NUM; i++) {
      pixels.setPixelColor(i, pixels.Color(0, 0, 0));
    }
  }
  headlights = enabled;
  pixels.show();
}

void setWarningLights(bool enabled) {
  warningLights = enabled;
  if(!enabled) {
    warningLightsEnabled(false);
  }
}

void warningLightsEnabled(bool enabled) {
  if(enabled) {
    int fullBrightness = getBrightness(255);
    int lessBrightness = getBrightness(165);
       
    for(int i = HEADLIGHT_NUM; i < HEADLIGHT_NUM + 2; i++) {
      pixels.setPixelColor(i, pixels.Color(fullBrightness, lessBrightness, 0));
    }
  } else {
    for(int i = HEADLIGHT_NUM; i < HEADLIGHT_NUM + 2; i++) {
      pixels.setPixelColor(i, pixels.Color(0, 0, 0));
    }
  }
  
  warningLightsState = enabled;
  pixels.show();
}

int getBrightness(int max) {
  return max * brightness / 100;
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
  if(warningLights && millis() >= warningLightsTimer) {
    warningLightsEnabled(!warningLightsState);
    warningLightsTimer = millis() + (warningLightsEnabled ? 500 : 1000);
  }

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
      setHeadlights(enabled);
      return;
    }

    if(packet == "w") { // Set warning lights (true/false)
      bool enabled = receivedData.substring(1) == "1";
      setWarningLights(enabled);
      return;
    }

    if(packet == "b") { // Set light brightness (0-100)
      int value = receivedData.substring(1).toInt();
      brightness = value;
      setHeadlights(headlights); // reapply headlights for brightness adjustment. warning lights will adjust on next cycle
      return;
    }

    if(packet == "p") {
      pingReceiveTimer = millis() + 1000;
      return;
    }
  }
}
