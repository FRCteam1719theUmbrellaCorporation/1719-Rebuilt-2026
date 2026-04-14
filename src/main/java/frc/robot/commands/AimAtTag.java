package frc.robot.commands;

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

import java.util.Optional;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.LimelightHandler;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class AimAtTag extends Command {

  /** 
   * Creates a new AimAtTag. 
   * Aims the robot at the april tag so the user can move and keep track of the desired object
   */

  SwerveSubsystem m_drivebase;
  LimelightHandler m_LL;
  CommandXboxController m_Controller;
  int m_targetTagID;
  Timer TagOOBTimer;
  PIDController RotController;

  protected Translation2d i_scalar(final double X, final double Y) {
    final double r = Math.sqrt(Math.pow(X, 2) 
                             + Math.pow(Y, 2));
    final double scale_factor = Math.pow(
        MathUtil.clamp(r,0,1),
        Constants.OperatorConstants.JOYSTICK_SENSITIVITY_FACTOR);
    return new Translation2d((X*scale_factor)/(r+Constants.OperatorConstants.EPISLON)* -1,(Y*scale_factor)/(r+Constants.OperatorConstants.EPISLON)* -1);
  }
  
  public AimAtTag(SwerveSubsystem drivebase, LimelightHandler LL, CommandXboxController Controller, int tagID) {
    // Use addRequirements() here to declare subsystem dependencies.
    m_drivebase = drivebase;
    m_LL = LL;
    m_Controller = Controller;
    m_targetTagID = tagID;
    RotController = new PIDController(LimelightConstants.ROT_REEF_ALIGNMENT_P, 0.f, 0.f);

    addRequirements(drivebase, LL);
  }

  public AimAtTag(SwerveSubsystem drivebase, LimelightHandler LL, CommandXboxController Controller) {
    // Use addRequirements() here to declare subsystem dependencies.
    this(drivebase, LL, Controller, -1);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    TagOOBTimer = new Timer();
    TagOOBTimer.start();

    RotController.setSetpoint(0);
    RotController.setTolerance(LimelightConstants.AIM_AT_TAG_TOLERANCE);

    Robot.LLCounts ++;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    Optional<Double> outPut = m_targetTagID == -1 
      ? m_LL.getAngleFromHub() 
      : m_LL.getAngleFromTag(m_targetTagID);
    double rot = 0;

    if (outPut.isPresent()) {
      TagOOBTimer.reset();
      rot = RotController.calculate(outPut.get());
      // System.out.println(rot);
    }

    m_drivebase.drive(i_scalar(m_Controller.getLeftY(), m_Controller.getLeftX()), rot, true);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // TODO: tell user they lost tag
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (Robot.LLCounts > 0){
      Robot.LLCounts --;
    }
    return false;
    // return this.TagOOBTimer.hasElapsed(LimelightConstants.DONT_SEE_TAG_WAIT_TIME);
  }
}