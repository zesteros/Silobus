

/*
  Julio del 2017

  Sketch para el Robodyn D1 R2 con módulo WiFi ESP8266
  Dedicado a leer y escribir cuatro pines digitales para obtener la
  distancia leída y así poder contar la cantidad de personas en
  un autobús al monitorear dicha distancia en la bajada y subida.

  Autor: Angelo Loza
*/
#include <ESP8266WiFi.h>
/*
  Nombre y contraseña de la red a conectar
*/
int numerodeper=0;
const char* ssid = "SILOBUS_AP";
const char* pass = "bugatti123";
/*
  Nombre y contraseña del WiFi Robodyn (Arduino)
*/
const char* ssid_ap = "SILOBUS";
const char* pass_ap = "camion";
/*
  Puerto para acceder al Robodyn
*/
const int port = 10001;
/*
  Distancia leída cuando no se tiene actividad
  entre los sensores.
*/
const int backMeasure = 4;
const int frontMeasure = 6;
int psgrs=0;
long startTime=0;
/*
  Crea un servidor con el puerto
*/
WiFiServer server(port);
/*Direccion ip estática (cambiar si es necesario)*/
IPAddress ip(192, 168, 3, 100);
/*Máscara de red (24, cambiar si es necesario)*/
IPAddress mask(255, 255, 255, 0);
/*
  Puerta de enlace (Default Gateway, cambiar si es necesario): Para obtener la puerta de enlace en Windows
  presiona la tecla"Windows" Y la tecla "R" al mismo tiempo y escribe cmd, enseguida en aceptar,
  en la linea de comandos escribe "ipconfig" y luego enter, después buscar la LAN Inalámbrica y la
  puerta de enlace prederterminada y sobreescribir esta:
*/
IPAddress defaultGateway(192, 168, 3, 1);

void setup() {
  Serial.begin(9600);
  /*
    Establece el nombre y contraseña del módulo ESP8266
    además del canal (default 1) y si quiere esconder
    el nombre de la red.
  */
  WiFi.softAP(ssid_ap, pass_ap, 1, 0);
  /*Conectar a red WiFi*/
  WiFi.config(ip, defaultGateway, mask);
  Serial.print("Conectando a ");
  Serial.println(ssid);
  /*Trata de conectar a la red establecida*/
  WiFi.begin(ssid, pass);
  boolean isConnected;

  while (WiFi.status() != WL_CONNECTED) {
    if (millis() >= 8000) {
      Serial.println("Tiempo fuera de conexión");
      isConnected = false;
      break;
    }
    isConnected = true;
    delay(500);
    Serial.print(".");
  }
  if (isConnected) {
    Serial.println("Se ha conectado exitosamente.");
    Serial.println("WiFi conectado");

    /*Comienza el servidor*/
    server.begin();
    Serial.println("Servidor iniciado");

    /*Imprime la dirección y el puerto*/
    Serial.print("Para acceder a los datos ingresa esta dirección: ");
    Serial.print("http://");
    Serial.print(WiFi.localIP());
    Serial.println(":" + String(port, DEC));
  } else {
    Serial.println("No se ha podido conectar, tiempo fuera, puede acceder al servidor localmente");
    Serial.print("Para acceder a los datos conectese e ingrese esta dirección: ");
    Serial.print("http://");
    Serial.print(WiFi.localIP());
    Serial.println(":" + String(port, DEC));
  }
}
void loop() {
  /*¿Hay algún cliente que requiera los datos?*/
  calculatePassengers();
  WiFiClient client = server.available();
  if (client) {
    /*¿El cliente está conectado?*/
    if (client.connected()) {
      /*Concatena los datos a enviar*/
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
      client.println(datoAEnviar);
    }
    /*Cierra la conexión*/
    client.stop();
  }
}

void calculatePassengers() {
  float frontDistance = getDistance(5, 2);
  float backDistance = getDistance(0, 4);
  
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

