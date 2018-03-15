#include <SoftwareSerial.h>
SoftwareSerial SIM808(5, 4); //Seleccionamos los pines 4 como Rx y 5 como Tx
int psgrs = 0;
long startTime = 0;
int timeBetweenPassengers = 800;

void setup() {
  SIM808.begin(19200);
  Serial.begin(19200);
  turnOnGPS();
  pinMode(2, INPUT);
  pinMode(3, INPUT);
  delay(100);
}

void loop() {
  sendGPSCommand();
  String response = readSIM808Response();
  calculatePassengers();
  response = "#+" + response.substring(27, 36) + "+" + response.substring(39, 49) + "+" + String(psgrs, DEC) + "~";
  Serial.println(response);
}

void sendGPSCommand() {
  SIM808.write("AT+CGPSINF=0\r\n");
}

void turnOnGPS () {
  SIM808.write("AT+CGPSPWR=1\r\n");

  Serial.println(readSIM808Response());

  SIM808.write("AT+CGPSRST=1\r\n");
  Serial.println(readSIM808Response());
  delay(30000);
  SIM808.write("AT+CGPSSTATUS?\r\n");
  Serial.println(readSIM808Response());
}


void calculatePassengers() {
  if (millis() > startTime + timeBetweenPassengers) {
    if (digitalRead(3) == 0) {
      psgrs++;
    }
    if (digitalRead(2) == 0) {
      if (psgrs > 0)
        psgrs--;
    }
    startTime = millis();
  }
}

String readSIM808Response() {
  String response = SIM808.readString();
  //if (response.substring(response.length() - 4, response.length() - 2) == "OK")
  return response;
  //else return "ERROR";
}


