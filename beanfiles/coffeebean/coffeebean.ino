#define BREW_TIME 240000 //4 minutes
#define HEAT_TIME 1800000 //30 minutes
#define COFFEE_PIN 0
typedef enum
{
  COFFEE_START = '1',
  COFFEE_BREW  = '2',
  COFFEE_HEAT  = '3',
  COFFEE_OFF   = '4',
  COFFEE_RQ_STATE = '5',
  COFFEE_ECHO  = '6'
} COFFEE_MESSAGES_T;

COFFEE_MESSAGES_T curState = COFFEE_OFF;

bool isCoffeeStarted = false;
long coffeeStartTime = 0;

void setup() {
  Bean.enableWakeOnConnect(true);
  Bean.setBeanName("coffeebean");
  pinMode(COFFEE_PIN, OUTPUT);
  digitalWrite(COFFEE_PIN, LOW);
  delay(5000);
}


void loop() {
  // put your main code here, to run repeatedly:
  bool isConnected = Bean.getConnectionState();
  if(isConnected || isCoffeeStarted)
  {
    
    if(Serial.available())
    {
      char readIn = Serial.read();
      if(readIn == COFFEE_START) //start coffee
      {
          if(!isCoffeeStarted)
          {
            isCoffeeStarted = true;
            Bean.setLedGreen(75);
            delay(1000);
            Bean.setLedGreen(0);
            //trigger relay
            coffeeStartTime = millis();
            curState = COFFEE_BREW;
            digitalWrite(COFFEE_PIN, HIGH);
          }
          
          Serial.write(curState);//coffee is brewing
      }
      if(readIn == COFFEE_ECHO)
      {
        Serial.write(COFFEE_ECHO);
      }
      if(readIn == COFFEE_RQ_STATE)
      {
        Serial.write(curState);
      }
    }

    if(isCoffeeStarted)
    {
      if(millis() > coffeeStartTime + BREW_TIME && curState == COFFEE_BREW)
      {
        Bean.setLedRed (75);
        delay(1000);
        Bean.setLedRed(0);
        Serial.write(COFFEE_HEAT);
        curState = COFFEE_HEAT;
      }
      if(millis() > coffeeStartTime + HEAT_TIME)
      {
        isCoffeeStarted = false;
        Bean.setLed(0,0,0);
        Serial.write(COFFEE_OFF);
        curState = COFFEE_OFF;
        Bean.disconnect();
        digitalWrite(COFFEE_PIN, LOW);
        //disable relay
      }
    }
  }
  else
  {
    Bean.setLedBlue(75);
    delay(1000);
    Bean.setLed(0,0,0);
    Bean.sleep(0xFFFFFF);
  }
}
