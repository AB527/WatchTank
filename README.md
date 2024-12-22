# WatchTank: Smart Water Tank Monitoring System  

**WatchTank** is an IoT-based solution that enables users to monitor the water level in their tank using an Android app. The system uses Bluetooth connectivity for seamless real-time communication, helping users optimize water usage and avoid overflows or shortages.  

## Features  

- **Real-Time Water Level Monitoring**: Access accurate tank water level readings on your Android app.  
- **Bluetooth Connectivity**: Effortlessly connect the monitoring system to your phone via Bluetooth.  
- **User-Friendly Android App**: Simple and intuitive interface for data visualization and configuration.  

## Components Used  

### Hardware  
- **Ultrasonic Sensor (e.g., HC-SR04)**: Measures the water level in the tank.  
- **Microcontroller with Bluetooth Module (e.g., Arduino + HC-05/ESP32)**: Processes sensor data and communicates with the app via Bluetooth.  
- **Power Supply Unit**: Powers the microcontroller and sensors.  

### Software  
- **Embedded Code**: Firmware written in C++ (Arduino IDE) to control the hardware and transmit data.  
- **Android App**: Custom-designed app for water level monitoring. Built with **Java/Kotlin** using **Android Studio**.  

## How It Works  

1. **Sensor Setup**:  
   - The ultrasonic sensor is placed at the top of the water tank.  
   - It measures the distance to the water surface and calculates the water level based on the tank's dimensions.  

2. **Data Processing**:  
   - The microcontroller reads the sensor's data, calculates the water level percentage, and sends the data via Bluetooth.  

3. **App Connectivity**:  
   - Users pair their phone with the system via Bluetooth.  
   - The Android app receives the data and displays it on an intuitive interface.  

## Setup Guide  

### Hardware Installation  
1. Connect the ultrasonic sensor to the microcontroller:  
   - **VCC**: Connect to 5V (ESP32: 3.3V)  
   - **GND**: Connect to ground  
   - **TRIG**: Connect to a GPIO pin  
   - **ECHO**: Connect to another GPIO pin  

2. Connect the Bluetooth module (if separate, e.g., HC-05):  
   - **VCC**: Connect to 5V  
   - **GND**: Connect to ground  
   - **TXD**: Connect to RX pin of the microcontroller  
   - **RXD**: Connect to TX pin of the microcontroller  

3. Upload the embedded code to the microcontroller using the Arduino IDE.  

### Android App Setup  
1. Install the Android app APK on your device.  
2. Pair your phone with the Bluetooth module in your system.  
3. Open the app, select the device from the list of available Bluetooth devices, and connect.  
4. View real-time water level data and configure thresholds as needed.  

## Future Improvements  

- Add support for additional connectivity options like Wi-Fi or GSM for remote monitoring.  
- Incorporate data logging and visualization for historical usage trends.  
- Extend compatibility with iOS and other platforms.  

## License  

This project is open-source and licensed under the MIT License. Feel free to use, modify, and share!  

---

### For any questions or contributions, feel free to contact us!  
