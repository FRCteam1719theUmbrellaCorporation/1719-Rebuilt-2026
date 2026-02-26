# 1719 Rebult 2026

This is our code for our robot _____ for the FRC 2026 game: Rebuilt

## The Game:

This game involves collecting and shooting balls, known as fuel, into a hub area. There is an auto period at the beginning of the game, which decides who is able to shoot into the hub first. While there's a lot less in this game compared to previous years, there's a lot more strategy and potential design variations involved, which make this a unique challenge!

## Features:

Intake: sucks fuel into our hopper area
Outake: has a funnel which directs balls into the shooter where they get launched towards the hub.
Todo :)

## CAN IDs

Our Swerve moduals are labeled as such:
The 10s digit of the id labels the moduel (1x would be front left)
while x2 represents our angling motor, x1 is the drive motor, and x0 is the encoder:

| Motor Label   | Motor Location | Device Type |  CAN ID |
| ------------- | -------------- | ----------- | --------|
| Front Left Drive Motor      | Left Front Drive     | CTRE Kraken X60 | 11      |
| Front Left Angle Motor      | Left Front Rotation Motor    | CTRE Kraken X60 | 12      |
| Front Left Angle Encoder      | Left Front CANCoder    | CANCoder | 10      |
| Front Right Drive Motor      | Right Front Drive    | CTRE Kraken X60 | 21      |
| Front Right Angle Motor      | Right Front Rotation Motor       | CTRE Kraken X60 | 22     |
| Front Right Angle Encoder    | Right Front CANCoder       | CANCoder | 20      |
| Back Left Drive Motor      | Left Back Drive    | CTRE Kraken X60 | 31      |
| Back Left Angle Motor     | Left Back Rotation Motor | CTRE Kraken X60 | 32      |
| Back Left Angle Encoder      | Left Back CANCoder | CANCoder | 30    |
| Back Right Drive Motor    | Right Back Drive | CTRE Kraken X60 | 36      |
| Back Right Angle Motor     | Right Back Rotation Motor | CTRE Kraken X60 | 37      |
| Back Right Angle Encoder     | Right Back CANCoder | CANCoder | 40      |
| Pigeon 2     | Gyro Port + Add location | Pigeon2 | 2      |
| Intake Motor     | Front of the robot | Rev Neo + Sparkmax | 3      |
| Funnel Motor    | Middle of the robot | Rev Neo + Sparkmax | 4     |
| Shooter Motor 1    | Left side of shooter from front | Rev Neo + Sparkmax | 5     |
| Shooter Motor 2     | Right side of shooter from front | Rev Neo + Sparkmax | 6      |

## Credits:

This code is based off of YAGSL's example code, which can be found [here](https://github.com/Yet-Another-Software-Suite/YAGSL)
Our drive code this year is also a continuation of our previous robot, Nessie.
Drive to tag command is based around team 1954 ElectroBunny's drive to reef code, which can be found [here](https://github.com/ElectroBunny/BetaBot2025) 

Programmers: 
Will do this later because I wanna make this section fun
