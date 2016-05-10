#define BREW_TIME 240000 //4 minutes
#define HEAT_TIME 1800000 //30 minutes

typedef enum
{
  COFFEE_START = 0x01,
  COFFEE_BREW  = 0x02,
  COFFEE_HEAT  = 0x03,
  COFFEE_OFF   = 0x04,
  COFFEE_RQ_STATE = 0x05,
  COFFEE_ECHO  = 0x06
} COFFEE_MESSAGES_T;

COFFEE_MESSAGES_T curState = COFFEE_OFF;

bool isCoffeeStarted = false;
long coffeeStartTime = 0;

void setup() {
  Bean.enableWakeOnConnect(true);
  Bean.setBeanName("coffeebean");
}


void loop() {
  // put your main code here, to run repeatedly:
  if(Bean.getConnectionState())
  {
    if(Serial.available())
    {
      char readIn = Serial.read();
      if(readIn == COFFEE_START) //start coffee
      {
          if(!isCoffeeStarted)
          {
            isCoffeeStarted = true;
            Bean.setLed(0, 75, 0);
            //trigger relay
            coffeeStartTime = millis();
            curState = COFFEE_BREW;
          }
          
          Serial.print(COFFEE_BREW);//coffee is brewing
      }
      if(readIn == COFFEE_ECHO)
      {
        Serial.print(COFFEE_ECHO);
      }
      if(readIn == COFFEE_RQ_STATE)
      {
        Serial.print(curState);
      }
    }

    if(isCoffeeStarted)
    {
      if(millis() > coffeeStartTime + BREW_TIME)
      {
        Bean.setLed(75, 0, 0);
        Serial.print(COFFEE_HEAT);
        curState = COFFEE_HEAT;
      }
      if(millis() > coffeeStartTime + HEAT_TIME)
      {
        isCoffeeStarted = false;
        Bean.setLed(0,0,0);
        Serial.print(COFFEE_OFF);
        curState = COFFEE_OFF;
        Bean.disconnect();
        //disable relay
      }
    }
  }
  else
  {
    Bean.setLedBlue(75);
    delay(1000);
    Bean.setLed(0,0,0);
    Bean.sleep(0xFFFFFFFF);
  }
}
