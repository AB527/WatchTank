#include <SoftwareSerial.h>
SoftwareSerial Bluetooth(2,3);// RX,TX
int list[] = {50, 20, 5}, count  = 0;

char IncomingVal;
void setup() 
{
  Serial.begin(9600);  
  Bluetooth.begin(9600);       
}

void loop()
{
  if(Bluetooth.available() > 0)  
  {
    IncomingVal = Bluetooth.read();        
    if(IncomingVal == '1') {          
      Bluetooth.println(list[count]);
      count++;
      if(count > 2) count = 0;
    }  
  }                            
} 
