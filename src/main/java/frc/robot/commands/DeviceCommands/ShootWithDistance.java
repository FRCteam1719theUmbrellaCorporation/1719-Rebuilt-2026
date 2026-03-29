// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.DeviceCommands;

import java.util.Optional;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LimelightHandler;
import frc.robot.subsystems.devices.OutakeSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class ShootWithDistance extends Command {
  private final OutakeSubsystem m_SHOOTER;
  private final LimelightHandler m_LL;
  private final int m_TARGET;
  private double power = .65d;
  private Timer hasntSeenShooter;

  /** Creates a new ShootWithDistance. */
  public ShootWithDistance(OutakeSubsystem m_outake, LimelightHandler m_ll, int targetTag) {
    this.m_SHOOTER = m_outake;
    this.m_LL = m_ll;
    this.m_TARGET = targetTag; 
    addRequirements(m_outake, m_ll);
  }

  public ShootWithDistance(OutakeSubsystem m_outake, LimelightHandler m_ll) {
    this(m_outake, m_ll, -1);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.m_SHOOTER.startShooter();
    this.hasntSeenShooter = new Timer();
    this.hasntSeenShooter.start();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    Optional<Double> distanceFromTag = m_TARGET == -1 ? m_LL.getDistFromHub() : m_LL.getDistFromTag(m_TARGET);
    if (distanceFromTag.isPresent()) {
      power = m_SHOOTER.ScailPower(distanceFromTag.get());
      hasntSeenShooter.reset();
      // System.out.println(power);
    } 
    m_SHOOTER.setShooterSpeed(power*1.0);
      
    //else if (hasntSeenShooter.hasElapsed(OutakeConstants.ShooterScailTimeout)) {
      //m_SHOOTER.stop();
    //}
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_SHOOTER.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
