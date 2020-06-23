String receivedData, packet;

long voltageTimer, compassTimer;
long pingSendTimer, pingReceiveTimer;

boolean handshakeCompleted;
long handshakeTimer;

void loopCommunicationTimers() {
  if(handshakeCompleted) {
    if(millis() >= pingReceiveTimer) {
      reset();
      return;
    }

    if(millis() >= pingSendTimer) {
      Serial.println("p");
      pingSendTimer = millis() + 500;
    }

    if(millis() >= compassTimer) {
      mpu.update();
      
      Serial.print("C");
      Serial.println(mpu.getYaw(), 2);
      compassTimer = millis() + COMPASS_INTERVAL;
    }

    if(!gpsFault && millis() >= maxLocationTime) {
      gpsFault = true;
      Serial.println("lf");
    }
  } else if(millis() >= handshakeTimer) {
    Serial.println("H");
    handshakeTimer = millis() + 1000;
  }
}

void resetCommunication() {
  handshakeCompleted = false;
}

void serialAvailable() {
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
