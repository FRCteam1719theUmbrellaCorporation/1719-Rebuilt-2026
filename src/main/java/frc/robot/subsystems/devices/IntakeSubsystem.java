// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.devices;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
  /** Creates a new Intake. */

  SparkMax IntakeMotor;

  public IntakeSubsystem() {
    IntakeMotor = new SparkMax(IntakeConstants.ID, MotorType.kBrushless);
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

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
