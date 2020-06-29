#define LED_PIN 7
#define LED_NUM 18
#define HEADLIGHT_NUM 16
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(LED_NUM, LED_PIN, NEO_GRB + NEO_KHZ800);

int brightness = 100;

boolean warningLightsEnabled, warningLightsState;
long warningLightsTimer;

boolean headlightsEnabled;

void setupLights() {
  pixels.begin();
  pixels.show();
}

void loopLights() {
  if(warningLightsEnabled && millis() >= warningLightsTimer) {
    warningLightsState = !warningLightsState;
    applyWarningLightsState();

    warningLightsTimer = millis() + (warningLightsState ? 500 : 1000);
  }
}

void setWarningLightsEnabled(boolean enabled) {
  warningLightsEnabled = enabled;

  if(!warningLightsEnabled) {
    warningLightsState = false;
    applyWarningLightsState();
  }
}

void setHeadlightsEnabled(boolean enabled) {
  headlightsEnabled = enabled;
  
  if(headlightsEnabled) {
    setLights(0, HEADLIGHT_NUM, 255, 255, 255);
  } else {
    setLights(0, HEADLIGHT_NUM, 0, 0, 0);
  }
}

void applyWarningLightsState() {
  if(warningLightsState) {
    setLights(HEADLIGHT_NUM, HEADLIGHT_NUM + 2, 255, 165, 0);
  } else {
    setLights(HEADLIGHT_NUM, HEADLIGHT_NUM + 2, 0, 0, 0);
  } 
}

void setBrightness(int value) {
  brightness = value;
  setHeadlightsEnabled(headlightsEnabled); // The headlights will stay in the same state, but the new brightness will be applied
}

/* Util methods */
int getBrightness(int max) {
  return max * brightness / 100;
}

void setLights(int start, int end, int r, int g, int b) {
  int rBrightness = getBrightness(r);
  int gBrightness = getBrightness(g);
  int bBrightness = getBrightness(b);
  
  for(int i = start; i < end; i++) {
    pixels.setPixelColor(i, pixels.Color(rBrightness, gBrightness, bBrightness));
  }
  pixels.show();
}
