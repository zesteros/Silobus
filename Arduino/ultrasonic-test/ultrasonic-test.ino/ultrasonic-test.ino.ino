const int trigPin = 2;
const int echoPin = 3;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(4, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  //Serial.print("distancia:");
  //Serial.print(getDistance());
  //Serial.println("cm");
  if (getDistance() > 8.5f || getDistance() < 7)
    digitalWrite(4, HIGH);
  else digitalWrite(4, LOW);
  delay(1000);
}

float getDistance() {
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

