// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.devices;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.OutakeConstants;

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
  RelativeEncoder OutakeEncoder;
  SparkClosedLoopController SparkLoop;
  SparkMaxConfig motorConfig;

  public OutakeSubsystem() {
    OutakeMotor = new SparkMax(OutakeConstants.SHOOTER_ID, MotorType.kBrushless);
    FunnelMotor = new SparkMax(OutakeConstants.FUNNEL_ID, MotorType.kBrushless);
    this.OutakeEncoder = OutakeMotor.getEncoder();
    // OutakeEncoder.getPosition()
    funnelTimer = new Timer();
    funnelTimer.start();
    this.isShooting = false;
    this.SparkLoop = OutakeMotor.getClosedLoopController();

    this.motorConfig = new SparkMaxConfig();

    // kslot1 is the velocity according to rev
    // https://github.com/REVrobotics/REVLib-Examples/blob/main/Java/SPARK/Closed%20Loop%20Control/src/main/java/frc/robot/Robot.java
    motorConfig.closedLoop
      .p(OutakeConstants.Shooter_P, ClosedLoopSlot.kSlot1)
      .i(OutakeConstants.Shooter_I, ClosedLoopSlot.kSlot1)
      .d(OutakeConstants.Shooter_D, ClosedLoopSlot.kSlot1)
      .outputRange(-1, 1, ClosedLoopSlot.kSlot1)
      .feedForward.kV(12.0 / 5767, ClosedLoopSlot.kSlot1); // idk if this is right or not but rev had it

    OutakeMotor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
    SmartDashboard.setDefaultNumber("ShooterTargetSpeed", 0);
    SmartDashboard.setDefaultNumber("EncoderVel", 0);

  }

  public void ConstantShoot(float input) {
    startShooter();
  }

  public void setVelocityShoot(float input) {
    // OutakeMot
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

  @Override
  public void periodic() {
    if (this.isShooting) {
      if (funnelTimer.hasElapsed(OutakeConstants.OUTAKE_TIME)) {
        FunnelMotor.set(OutakeConstants.FUNNEL_SPEED);
      }

      double targetSpeed = SmartDashboard.getNumber("ShooterTargetSpeed", 0);
      SparkLoop.setSetpoint(targetSpeed, ControlType.kVelocity, ClosedLoopSlot.kSlot1);
      SmartDashboard.putNumber("Encoder Vel", OutakeEncoder.getVelocity());
    }
  }
}
