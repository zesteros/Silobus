#include <LiquidCrystal.h>

LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

int psgrs=0;
long startTime=0;

void setup() {
  Serial.begin(9600);
  lcd.begin(16,2);

}
void loop() {
  /*¿Hay algún cliente que requiera los datos?*/
  calculatePassengers();
  
      String datoAEnviar = "";
      /*Símbolo para decir que se comienzan a enviar datos: '#'*/
      datoAEnviar += "#";
      /*Dato 1*/
      //float frontSensor = getDistance(5, 2);
      datoAEnviar += String(psgrs, DEC);
      /*Fin de dato 1: '+'*/
      datoAEnviar += "+";
      /*Dato 2*/
      //float backSensor = getDistance(0, 4);
      datoAEnviar += String(0, DEC);
      /*Fin de dato 2*/
      
      datoAEnviar += "+";
      /*Fin de envio de datos: '~'*/
      datoAEnviar += "~";
      /*Imprime a serial y envía a cliente*/
      Serial.println(datoAEnviar);
    //for(i=0;i<16;i++){
      lcd.setCursor(6,0);
      lcd.print("RUTA");
      lcd.setCursor(1,1);
      lcd.print("X-62 AUXILIAR");
      /*  for(int j = 0;j<13;j++){
            if(j+i<16){
              lcd.setCursor(j+i,1);
              lcd.print(route[j]);
            }else{
              lcd.setCursor(j+i-16,1);
              lcd.print(route[j]);  
            }
          }          lcd.clear();
      }*/
      delay(600);
 
}

void calculatePassengers() {
  float frontDistance = getDistance(8, 9);
  float backDistance = getDistance(6, 7);
  
  if(millis() > startTime + 1500){
  if (frontDistance > 6 || frontDistance < 3) {
    psgrs++;
  }
 if (backDistance >  5 || backDistance < 3) {
    
  if(psgrs>0)
    psgrs--;
  } 
  startTime = millis();
  }
  /*Serial.print("front:");
    Serial.println(frontDistance);
    Serial.print("back:");
    Serial.println(backDistance);
    Serial.print("people:");
    Serial.println(psgrs);
    Serial.print("starttime:");
    Serial.println(startTime);
    Serial.print("millis");
    Serial.println(millis());*/
}

float getDistance(int trigPin, int echoPin) {
  pinMode(trigPin, OUTPUT);
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  pinMode(echoPin, INPUT);
  float duration = pulseIn(echoPin, HIGH);
  return microsecondsToCentimeters(duration);
}

float microsecondsToCentimeters(float microseconds) {
  return microseconds / 29 / 2;
}

