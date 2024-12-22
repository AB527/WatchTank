// Sound of time take 343 m/s
// Trigger = In, Echo = Out

#include <SoftwareSerial.h>
#include <NewPing.h>

int WLSensorTrig = 2, WLSensorEcho = 3, WLSensorMaxDistance = 200;
double WLDistanceTime, WLDistance;
char BluetoothText;

SoftwareSerial Bluetooth(5,4); // Rx=5, Tx=4
NewPing sonar(WLSensorTrig, WLSensorEcho, WLSensorMaxDistance);

void setup() {
  Serial.begin(9600);  
  Bluetooth.begin(9600); 
}

void loop() {
  if(Bluetooth.available() > 0)  
  {
    BluetoothText = Bluetooth.read();      
    Serial.print(BluetoothText); 
    handleCommand(BluetoothText);              
  }
}

void handleCommand(char commandCode) {
  switch(commandCode) {
    case '1':
      getWaterLevel();
      break;
    default:
      break;
  }
}

void getWaterLevel() {
  double WLDistance = 0;

  for(int i = 0;i < 5;i++) {
    delay(250);
    unsigned int WLDistanceTime = sonar.ping();
    WLDistance += (WLDistanceTime*0.0343)/2;
  }  

  WLDistance /= 5;
  Bluetooth.println(WLDistance);
}
