// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.devices;

import com.revrobotics.ColorSensorV3;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
  /** Creates a new Intake. */

  SparkMax IntakeMotor;
  ColorSensorV3 ColorSensor;

  public IntakeSubsystem() {
    IntakeMotor = new SparkMax(IntakeConstants.ID, MotorType.kBrushless);
    ColorSensor = new ColorSensorV3(I2C.Port.kOnboard);
  }

  public void setSpeed(float input) {
    IntakeMotor.set(input);
  }

  public void stop() {
    IntakeMotor.set(0);
  }

  public void outake(float input) {
    setSpeed(-input);
  }

  public boolean hasBall(){
    if (ColorSensor.getProximity() >= IntakeConstants.WALL_DIST){
      return true;
    }
    else{return false;}
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    System.out.println(ColorSensor.getProximity());
    // if (!hasBall()){
      
    // }
  }
}
