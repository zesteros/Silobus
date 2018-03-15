
#include <LiquidCrystal.h>

// initialize the library with the numbers of the interface pins
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

void setup() {
  // set up the LCD's number of columns and rows:
  // Print a message to the LCD.
    lcd.begin(16, 2);
    pinMode(6,INPUT);
}

void loop() {
  // set the cursor to column 0, line 1
  // (note: line 1 is the second row, since counting begins with 0):
  // print the number of seconds since reset:
  //lcd.print(millis() / 1000);
  lcd.setCursor(0, 0);
  
  lcd.print("hello, world!");
    lcd.setCursor(0, 1);

  lcd.print(digitalRead(6));
  delay(200);
  lcd.clear();
  
}
