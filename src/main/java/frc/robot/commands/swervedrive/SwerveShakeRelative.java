// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.swervedrive;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class SwerveShakeRelative extends Command {
  SwerveSubsystem m_SwerveSubsystem;
  private double setSpeed;
  private Timer delayTimer;

  /** Creates a new SwerveShakeRelative. */
  public SwerveShakeRelative(SwerveSubsystem m_SwerveSubsystem) {
    this.m_SwerveSubsystem = m_SwerveSubsystem;
    addRequirements(m_SwerveSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    delayTimer = new Timer();
    delayTimer.start();
    setSpeed = OperatorConstants.SHAKE_POWER_X;
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (delayTimer.hasElapsed(.1)) {
        setSpeed = -setSpeed;
        delayTimer.reset();
      }
    m_SwerveSubsystem.drive(new Translation2d(), setSpeed, true);
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