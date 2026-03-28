// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.devices;

import com.revrobotics.spark.SparkMax;

import java.util.Map;

import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.ControllerConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.OutakeConstants;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
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
  double funnelPower;
  final GenericEntry ShooterAdjustment;

  public OutakeSubsystem() {
    OutakeMotor = new SparkMax(OutakeConstants.SHOOTER_ID, MotorType.kBrushless);
    FunnelMotor = new SparkMax(OutakeConstants.FUNNEL_ID, MotorType.kBrushless);

    funnelTimer = new Timer();
    funnelTimer.start();
    this.isShooting = false;
    funnelPower = OutakeConstants.FUNNEL_SPEED;
    SmartDashboard.setDefaultNumber("Shooter-Power", OutakeConstants.OUTAKE_SPEED);

    final ShuffleboardTab ShooterTab = Shuffleboard.getTab("Shooter_Data");
    this.ShooterAdjustment = ShooterTab
      .add("Outtake Adjustment", 1)
      .withWidget(BuiltInWidgets.kNumberSlider)
      .withProperties(Map.of(
        "min", 1-ControllerConstants.TrimSwitchBounds, 
        "max", 1+ControllerConstants.TrimSwitchBounds))
      .getEntry();
  }

  public double ScailPower(double distance) {
    // this is a linear regression based of estimates shooting positions based on feet from goal and power applied to motors.
    // Distance is in meters
    return distance >= OutakeConstants.MinShootDistance 
      ? MathUtil.clamp(distance * OutakeConstants.DistancePowerMult + OutakeConstants.DistancePowerOffset, 
                      0, 
                      Constants.Motor_Max
                      )
      : 0; 
  }

  public void startShooter() {
    this.isShooting = true;
    funnelTimer.reset();
    // BloaderLoop.setSetpoint(OutakeConstants.BloaderVel, ControlType.kVelocity, ClosedLoopSlot.kSlot0);
  }

  public void stop() {
    this.isShooting = false;
    OutakeMotor.set(0);
    FunnelMotor.set(0);
    // BloaderLoop.setSetpoint(0, ControlType.kVelocity, ClosedLoopSlot.kSlot0);
  }

  public void setShooterSpeed(double val) {
    // this.OutakeMotor.set(val);
    this.OutakeMotor.set(val * ShooterAdjustment.getDouble(1));
  }

  public void ConstantShoot(float input) {
    startShooter();
    setShooterSpeed(input);
    setFunnelPower(OutakeConstants.FUNNEL_SPEED);
  }

  public void setFunnelPower(double input) {
    funnelPower = input;
  }

  public void adjustTrim(double input) {
    ShooterAdjustment.setDouble(MathUtil.clamp(ShooterAdjustment.getDouble(OperatorConstants.SlowDriveFactor) + input, 
                          ControllerConstants.TrimSwitchLow,
                          ControllerConstants.TrimSwitchHigh));
  }

  /**
   * Reverses the outake
   * Despite the name you still need to use a negative value
   * 
   * @param ShooterInput
   */
  public void reverseOutake(float ShooterInput) {
    reverseOutake(ShooterInput, OutakeConstants.FUNNEL_SPEED);
    setFunnelPower(-OutakeConstants.FUNNEL_SPEED);
  }

  /**
   * Reverses the outake
   * Despite the name you still need to use a negative value
   * 
   * @param ShooterInput: Shooter power
   * @param FunnelInput: Funnel power. defaults to OutakeConstants.FUNNEL_SPEED
   */
  public void reverseOutake(float ShooterInput, float FunnelInput) {
    setFunnelPower(FunnelInput);
    this.isShooting = true; // funnel should move at the same time to avoid damaging fuel
    setShooterSpeed(ShooterInput);
  }
  
  public void outake(float input) {
    ConstantShoot(-input);
  }

  @Override
  public void periodic() {
    if (this.isShooting) {
      // double smdb = SmartDashboard.getNumber("Shooteer-Power", OutakeConstants.OUTAKE_SPEED);
      // OutakeMotor.set(smdb);
      if (funnelTimer.hasElapsed(OutakeConstants.OUTAKE_TIME)) {
        FunnelMotor.set(funnelPower);
      }
    }
  }
}

