// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.swervedrive;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.LimelightConstants;
import frc.robot.Constants.OutakeConstants;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class SwerveShakeRelative extends Command {
  SwerveSubsystem m_SwerveSubsystem;
  private PIDController xController;
  private double setPoint;
  private boolean isForward;

  /** Creates a new SwerveShakeRelative. */
  public SwerveShakeRelative(SwerveSubsystem m_SwerveSubsystem) {
    this.m_SwerveSubsystem = m_SwerveSubsystem;
    addRequirements(m_SwerveSubsystem);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    setPoint = m_SwerveSubsystem.getPose().getX() + OutakeConstants.SHAKE_DISTANCEX;
    isForward = true;
    xController = new PIDController(LimelightConstants.ROT_REEF_ALIGNMENT_P, 0, 0);
    xController.setTolerance(LimelightConstants.ROT_TOLERANCE_REEF_ALIGNMENT);
    xController.setSetpoint(setPoint);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (xController.atSetpoint()) {
      isForward = !isForward;
      setPoint += OutakeConstants.SHAKE_DISTANCEX * (!isForward ? -1 : 1); 
      xController.setSetpoint(setPoint);
    }
    double output = xController.calculate(m_SwerveSubsystem.getPose().getX());
    m_SwerveSubsystem.drive(new Translation2d(output, 0), 0, false);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
