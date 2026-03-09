// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.devices;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.OutakeConstants;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class OutakeSubsystem extends SubsystemBase {
  /** 
   * This class handles the outake. this includes the shooter and the funnel on our robot
   * This subsystem wont be entirely in charge of setting the shooter and what not, however, it is in charge of funnel
   * Funnel activates after the shooter is warmed up.
   * 
   * TODO: There's a command that changes the speed of the shooter based on desires output thats where the limelight micromanages output
   * BUT VERY TODO :)
   * 
  */

  private Timer funnelTimer;
  SparkMax OutakeMotor;
  SparkMax FunnelMotor;
  boolean isShooting;

  public OutakeSubsystem() {
    OutakeMotor = new SparkMax(OutakeConstants.SHOOTER_ID, MotorType.kBrushless);
    FunnelMotor = new SparkMax(OutakeConstants.FUNNEL_ID, MotorType.kBrushless);
    funnelTimer = new Timer();
    funnelTimer.start();
    this.isShooting = false;
    SmartDashboard.setDefaultNumber("Shooter-Power", OutakeConstants.OUTAKE_SPEED);
  }

  public double ScailPower(double distance) {
    // this is a linear regression based of estimates shooting positions based on feet from goal and power applied to motors.
    // PURE SPECULATION! In theory this should map our shooter to distance 
    return distance >= OutakeConstants.MinShootDistance 
      ? MathUtil.clamp(distance * OutakeConstants.DistancePowerMult + OutakeConstants.DistancePowerOffset * -1, 
                      Constants.Motor_Min, 
                      Constants.Motor_Max
                      )
      : 0; 
  }

  public void setShooterSpeed(double val) {
    this.OutakeMotor.set(val);
  }

  public void ConstantShoot(float input) {
    startShooter();
    OutakeMotor.set(input);
  }

  public void startShooter() {
    this.isShooting = true;
    funnelTimer.reset();
  }

  public void stop() {
    this.isShooting = false;
    OutakeMotor.set(0);
    FunnelMotor.set(0);
  }
  
  public void outake(float input) {
    ConstantShoot(-input);
  }

  @Override
  public void periodic() {
    if (this.isShooting) {
      double smdb = SmartDashboard.getNumber("Shooter-Power", OutakeConstants.OUTAKE_SPEED);
      OutakeMotor.set(smdb);
      if (funnelTimer.hasElapsed(OutakeConstants.OUTAKE_TIME)) {
        FunnelMotor.set(OutakeConstants.FUNNEL_SPEED);
      }
    }
  }
}

