
// Sound of time take 343 m/s
// Trigger = In, Echo = Out

#include <NewPing.h>

int WLSensorTrig = 2, WLSensorEcho = 3, WLSensorMaxDistance = 200;
double WLDistanceTime, WLDistance;

NewPing sonar(WLSensorTrig, WLSensorEcho, WLSensorMa5xDistance);

void setup() {
  Serial.begin(9600);
}

void loop() {
  delay(1000);
  getWaterLevel();
}

void getWaterLevel() {
  double WLDistance;

  for(int i = 0;i < 5;i++) {
    delay(250);
    unsigned int WLDistanceTime = sonar.ping();
    WLDistance += (WLDistanceTime*0.0343)/2;
  }  

  WLDistance /= 5;
  Serial.println(WLDistance);
}
