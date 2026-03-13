// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.DeviceCommands;

import java.util.Optional;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.OutakeConstants;
import frc.robot.MotionCompDashboard;
import frc.robot.subsystems.LimelightHandler;
import frc.robot.subsystems.devices.OutakeSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class ShootWithDistance extends Command {
  private final OutakeSubsystem m_SHOOTER;
  private final LimelightHandler m_LL;
  private final SwerveSubsystem m_drivebase;
  private final int m_TARGET;
  private double power = .65d;
  private Timer hasntSeenShooter;

  /** Creates a new ShootWithDistance. */
  public ShootWithDistance(OutakeSubsystem m_outake, LimelightHandler m_ll, int targetTag, SwerveSubsystem drivebase) {
    this.m_SHOOTER = m_outake;
    this.m_LL = m_ll;
    this.m_TARGET = targetTag;
    this.m_drivebase = drivebase;
    addRequirements(m_outake, m_ll); // drivebase not required — read-only velocity access
  }

  /** Hub-mode with motion compensation. */
  public ShootWithDistance(OutakeSubsystem m_outake, LimelightHandler m_ll, SwerveSubsystem drivebase) {
    this(m_outake, m_ll, -1, drivebase);
  }

  /** Hub-mode without motion compensation (motion comp switch will have no effect). */
  public ShootWithDistance(OutakeSubsystem m_outake, LimelightHandler m_ll) {
    this(m_outake, m_ll, -1, null);
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

      // Motion compensation — no effect when switch is off, robot is stationary, or no drivebase.
      if (m_drivebase != null && MotionCompDashboard.ENABLED.getBoolean(false)) {
        double d = distanceFromTag.get();
        double vBall = power * MotionCompDashboard.BALL_SPEED_AT_FULL_POWER.getDouble(OutakeConstants.BALL_SPEED_AT_FULL_POWER);
        double t = d / vBall;
        ChassisSpeeds vel = m_drivebase.getRobotVelocity();
        double effectiveDist = Math.max(d - vel.vxMetersPerSecond * t, OutakeConstants.MinShootDistance);
        power = m_SHOOTER.ScailPower(effectiveDist);
        SmartDashboard.putNumber("MotionComp/EffectiveDist_m", effectiveDist);
        SmartDashboard.putNumber("MotionComp/FlightTime_s", t);
      }
      hasntSeenShooter.reset();
      System.out.println(power);
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
