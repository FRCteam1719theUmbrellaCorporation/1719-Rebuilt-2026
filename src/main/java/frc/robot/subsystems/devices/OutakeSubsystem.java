// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.devices;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.OutakeConstants;

import edu.wpi.first.wpilibj.Timer;

public class IntakeSubsystem extends SubsystemBase {
  /** Creates a new Intake. */

  private Timer funnelTimer;

  SparkMax OutakeMotor;
  SparkMax FunnelMotor;

  public OutakeSubsystem() {
    OutakeMotor = new SparkMax(OutakeConstants.SHOOTER_ID, MotorType.kBrushless);
    FunnelMotor = new SparkMax(OutakeConstants.FUNNEL_ID, MotorType.kBrushless);
    funnelTimer = new Timer();
    funnelTimer.start();
  }

  public void setSpeed(float input) {
    funnelTimer.reset();
    OutakeMotor.set(input);
  }

  public void stop() {
    OutakeMotor.set(0);
    FunnelMotor.set(0);
  }

  @Override
  public void periodic() {
    if (funnelTimer.hasElapsed(OutakeConstants.OUTAKE_TIME)){
      FunnelMotor.set(OutakeConstants.FUNNEL_SPEED);
    }
  }
}
