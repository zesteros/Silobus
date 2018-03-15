#include <LiquidCrystal.h>

LiquidCrystal lcd(12, 11, 5, 4, 3, 2);
void setup() {
  // put your setup code here, to run once:
lcd.begin(16,2);


}

void loop() {
  // put your main code here, to run repeatedly:

  
    int i = 0;
    char route [] = "X-62 AUXILIAR";
    for(i=0;i<16;i++){
      lcd.setCursor(6,0);
      lcd.print("RUTA");
      
        for(int j = 0;j<13;j++){
            if(j+i<16){
              lcd.setCursor(j+i,1);
              lcd.print(route[j]);
            }else{
              lcd.setCursor(j+i-16,1);
              lcd.print(route[j]);  
            }
          }
          delay(1000);
          lcd.clear();
      }
   
}
