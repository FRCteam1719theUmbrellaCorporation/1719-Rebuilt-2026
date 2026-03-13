package frc.robot.commands;

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

import java.util.Optional;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.LimelightHandler;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class AimAtTagAuto extends Command {

  /** 
   * Creates a new AimAtTag For Auto. 
   * Aims the robot at the april tag so the user
   * 
   * Driver cannot control this version with driving, so this version 
   * is reserved for autos!!
   */

  SwerveSubsystem m_drivebase;
  LimelightHandler m_LL;
  int m_targetTagID;
  Timer TagOOBTimer;
  Timer isAtSetpointTimer;
  PIDController RotController;
  
  public AimAtTagAuto(SwerveSubsystem drivebase, LimelightHandler LL, int tagID) {
    // Use addRequirements() here to declare subsystem dependencies.
    m_drivebase = drivebase;
    m_LL = LL;
    m_targetTagID = tagID;
    RotController = new PIDController(LimelightConstants.ROT_REEF_ALIGNMENT_P, 0.f, 0.f);

    addRequirements(drivebase, LL);
  }

  public AimAtTagAuto(SwerveSubsystem drivebase, LimelightHandler LL) {
    this(drivebase, LL, -1);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    TagOOBTimer = new Timer();
    TagOOBTimer.start();
    isAtSetpointTimer = new Timer();
    isAtSetpointTimer.start();

    RotController.setSetpoint(0);
    RotController.setTolerance(LimelightConstants.AIM_AT_TAG_TOLERANCE);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    Optional<Double> outPut =  m_targetTagID == -1 
      ? m_LL.getAngleFromHub() 
      : m_LL.getAngleFromTag(m_targetTagID);
    double rot = 0;

    if (outPut.isPresent()) {
      TagOOBTimer.reset();
      rot = RotController.calculate(outPut.get());
    }

    if (!RotController.atSetpoint()) {
      isAtSetpointTimer.reset();
    }

    m_drivebase.drive(new Translation2d(), rot, true);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (TagOOBTimer.hasElapsed(LimelightConstants.DONT_SEE_TAG_WAIT_TIME)) {
      System.out.println("Terminated early because tag was not seen");
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return TagOOBTimer.hasElapsed(LimelightConstants.DONT_SEE_TAG_WAIT_TIME)
          || isAtSetpointTimer.hasElapsed(LimelightConstants.POSE_VALIDATION_TIME);
  }
}