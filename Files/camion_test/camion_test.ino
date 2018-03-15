int psgrs = 0;
long startTime;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(6, OUTPUT);
  startTime = millis();
}

void loop() {
  // put your main code here, to run repeatedly:
  float frontDistance = getDistance(2, 3);
  float backDistance = getDistance(4, 5);
  //if(millis() > startTime + 1500){
  if (frontDistance > 6 || frontDistance < 3) {
    digitalWrite(6, HIGH);
    psgrs++;
  }
  else if (backDistance >  5 || backDistance < 3) {
    digitalWrite(6, HIGH);
    psgrs--;
  } else digitalWrite(6, LOW);
  //startTime = millis();
  //}
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
  delay(500);
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
