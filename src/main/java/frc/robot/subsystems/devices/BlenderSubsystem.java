// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.devices;

import com.revrobotics.spark.SparkMax;

import java.util.Map;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
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

public class BlenderSubsystem extends SubsystemBase {
  /** 
   * This handles the blender in the storage
   * very simple
   * 
  */
  SparkMax BlenderMotor;
  SparkClosedLoopController BloaderLoop;

  public BlenderSubsystem() {
    BlenderMotor = new SparkMax(OutakeConstants.BLENDER_ID, MotorType.kBrushless);
    BloaderLoop = BlenderMotor.getClosedLoopController();
  }

  public void setBlenderRPM(double RPM) {
    BloaderLoop.setSetpoint(RPM, ControlType.kVelocity, ClosedLoopSlot.kSlot0);
  }
}

