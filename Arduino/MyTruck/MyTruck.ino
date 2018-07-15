#include <NewPing.h>        //Ultrasonic sensor function library. You must install this library
#include <SoftwareSerial.h>

//motor
#define Motor_Left_Pin1 2 
#define Motor_Left_Pin2 3 
#define Motor_Right_Pin1 4
#define Motor_Right_Pin2 5

//distance sensors
#define US_Left_Trig A4
#define US_Left_Echo A2 
#define US_Right_Trig A1 
#define US_Right_Echo A0 

//indicators
#define Indicator_Front_Left 12
#define Indicator_Front_Right 13 

#define Indicator_Stop_Left A3
#define Indicator_Back_Left 7

#define Indicator_Stop_Right 8
#define Indicator_Back_Right 9 

//horn
#define Buzzer 6

//distance
#define Maximum_Distance_Threshold 10
int curDistanceLeft;
int curDistanceRight;

//command
char command;

//US sensors
NewPing leftUsSensor (US_Left_Trig, US_Left_Echo); //sensor function
NewPing rightUsSensor (US_Right_Trig, US_Right_Echo); 

//Bluetooth
SoftwareSerial btSerial(10, 11); // RX, TX

void setup() {
  pinMode(Motor_Left_Pin1, OUTPUT); 
  pinMode(Motor_Left_Pin2, OUTPUT); 
  pinMode(Motor_Right_Pin1, OUTPUT); 
  pinMode(Motor_Right_Pin2, OUTPUT); 
  pinMode(Indicator_Front_Left, OUTPUT);
  pinMode(Indicator_Front_Right, OUTPUT); 
  pinMode(Indicator_Back_Left, OUTPUT);
  pinMode(Indicator_Back_Right, OUTPUT); 
  pinMode(Indicator_Stop_Left, OUTPUT);
  pinMode(Indicator_Stop_Right, OUTPUT); 
  pinMode(Buzzer, OUTPUT);
  
  curDistanceLeft = 0;
  curDistanceRight = 0;

  Serial.begin(9600);
  btSerial.begin(9600);
  btSerial.println("Hello, world?");
  
  performDemo();
}

void loop() { 
  listenToApp();
}

void listenToApp(){
  if (btSerial.available())  //if no command, do nothing.
    command = btSerial.read();
  
  Serial.println(command);
  switch(command){
    case 'F':
      moveForward();
      break;
    case 'B':
      moveBackward();
      break;
    case 'R':
      turnRight();
      break;
    case 'L':
      turnLeft();
      break;
    case 'Z':
      stopTruck();
      break;
    default:
      stopTruck();
      break;  
  }  
}


void performDemo(){
  //blink all lights together
//  for(int i=0;i<4;i++){
//   blinkRightIndicators();
//   blinkLeftIndicators();
//  }
//  
//  
//  moveForward();
//  delay(500);
//  stopTruck();
//  delay(500);
//  moveBackward();
//  delay(500);
//  stopTruck();
//  delay(500);
//  turnRight();
//  delay(500);
//  stopTruck();
//  delay(500);
//  turnLeft();
//  delay(500);
//  stopTruck();
//  delay(500);

  for(int i=0;i<3;i++){
    blinkAllIndicators();
    beep(150, 150);
  }
  delay(100);
  moveForward();
  delay(200);
  stopTruck();
  delay(500);
  moveBackward();
  delay(200);
  stopTruck();
  delay(10);
}

void beep(unsigned char duration, unsigned char interval){
  analogWrite(Buzzer, 64);     //write pwm
  delay(duration);          // wait for a delayms ms
  digitalWrite(Buzzer, LOW);       // 0 turns it off
  delay(interval);          // wait for a delayms ms   
}  

void stopTruck(){
  //drive motors
  digitalWrite(Motor_Left_Pin1, LOW); 
  digitalWrite(Motor_Left_Pin2, LOW); 
  digitalWrite(Motor_Right_Pin1, LOW); 
  digitalWrite(Motor_Right_Pin2, LOW); 

  turnOffAllIndicators();
  turnOnBrakeLight();
}

void moveForward(){
  curDistanceLeft = leftUsSensor.ping_cm();
  curDistanceRight = rightUsSensor.ping_cm();

  if(curDistanceLeft <= Maximum_Distance_Threshold || curDistanceRight <= Maximum_Distance_Threshold){
    stopTruck();
    blinkAllIndicators();
    beep(1000, 100);
    return;
  }
    
  //drive motors
  digitalWrite(Motor_Left_Pin1, HIGH); 
  digitalWrite(Motor_Left_Pin2, LOW); 
  digitalWrite(Motor_Right_Pin1, HIGH); 
  digitalWrite(Motor_Right_Pin2, LOW); 

  turnOffBrakeLight();
  turnOffAllIndicators();
}

void moveBackward(){
  //drive motors
  digitalWrite(Motor_Left_Pin1, LOW); 
  digitalWrite(Motor_Left_Pin2, HIGH); 
  digitalWrite(Motor_Right_Pin1, LOW); 
  digitalWrite(Motor_Right_Pin2, HIGH); 

  turnOffAllIndicators();
  turnOnBrakeLight();
}

void turnLeft (){
  //drive motors
  digitalWrite(Motor_Left_Pin1, LOW); 
  digitalWrite(Motor_Left_Pin2, HIGH); 
  digitalWrite(Motor_Right_Pin1, HIGH); 
  digitalWrite(Motor_Right_Pin2, LOW); 

  turnOffBrakeLight();
  turnOffRightIndicators();
  blinkLeftIndicators();
  beep(50, 50);
}

void turnRight(){
  //drive motors
  digitalWrite(Motor_Left_Pin1, HIGH); 
  digitalWrite(Motor_Left_Pin2, LOW); 
  digitalWrite(Motor_Right_Pin1, LOW); 
  digitalWrite(Motor_Right_Pin2, HIGH); 

  turnOffBrakeLight();
  turnOffLeftIndicators();
  blinkRightIndicators();
  beep(50, 50);
}

void turnOnBrakeLight(){
  digitalWrite(Indicator_Stop_Left, HIGH);
  digitalWrite(Indicator_Stop_Right, HIGH); 
}

void turnOffBrakeLight(){
  digitalWrite(Indicator_Stop_Left, LOW);
  digitalWrite(Indicator_Stop_Right, LOW); 
}

void turnOffLeftIndicators(){
  digitalWrite(Indicator_Front_Left, LOW);
  digitalWrite(Indicator_Back_Left, LOW);  
}

void turnOffRightIndicators(){
  digitalWrite(Indicator_Front_Right, LOW);
  digitalWrite(Indicator_Back_Right, LOW);  
}

void turnOffAllIndicators(){
  turnOffLeftIndicators();
  turnOffRightIndicators();
}

void blinkAllIndicators(){
  digitalWrite(Indicator_Front_Right, HIGH);
  digitalWrite(Indicator_Back_Right, HIGH);
  digitalWrite(Indicator_Front_Left, HIGH);
  digitalWrite(Indicator_Back_Left, HIGH);
  digitalWrite(Indicator_Stop_Left, HIGH);
  digitalWrite(Indicator_Stop_Right, HIGH);
  delay(250); 
  digitalWrite(Indicator_Front_Right, LOW);
  digitalWrite(Indicator_Back_Right, LOW);
  digitalWrite(Indicator_Front_Left, LOW);
  digitalWrite(Indicator_Back_Left, LOW);
  digitalWrite(Indicator_Stop_Left, LOW);
  digitalWrite(Indicator_Stop_Right, LOW);
  delay(250); 
  
}

void blinkRightIndicators(){  
  digitalWrite(Indicator_Front_Right, HIGH);
  digitalWrite(Indicator_Back_Right, HIGH);
  delay(250); 
  digitalWrite(Indicator_Front_Right, LOW); 
  digitalWrite(Indicator_Back_Right, LOW); 
  delay(250);
}

void blinkLeftIndicators(){  
  digitalWrite(Indicator_Front_Left, HIGH);
  digitalWrite(Indicator_Back_Left, HIGH);
  delay(250); 
  digitalWrite(Indicator_Front_Left, LOW); 
  digitalWrite(Indicator_Back_Left, LOW);
  delay(250);
}

void blinkBrakeLights(){  
  digitalWrite(Indicator_Stop_Left, HIGH);
  digitalWrite(Indicator_Back_Left, HIGH);
  delay(250); 
  digitalWrite(Indicator_Stop_Left, LOW); 
  digitalWrite(Indicator_Stop_Right, LOW);
  delay(250);
}



