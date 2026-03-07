package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.*;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.LimelightHelpers;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.Constants.LimelightConstants;


public class Movetotag extends Command {
  public PIDController xController, yController, rotController;
  private boolean isRightScore;
  private Timer dontSeeTagTimer, stopTimer;
  private SwerveSubsystem drivebase;
  private double tagID = 3;
  public double initialXPos,initialYPos, initalRotPose;

  public Movetotag(boolean isRightScore, SwerveSubsystem drivebase) {
    xController = new PIDController(LimelightConstants.X_REEF_ALIGNMENT_P, 0.0, 0);  // Vertical movement
    yController = new PIDController(LimelightConstants.Y_REEF_ALIGNMENT_P, 0.0, 0);  // Horitontal movement
    rotController = new PIDController(LimelightConstants.ROT_REEF_ALIGNMENT_P, 0, 0);  // Rotation
    this.isRightScore = isRightScore;
    this.drivebase = drivebase;
    addRequirements(drivebase);
  }
  public double[] Computefinalstaticpose(){
    double[] postions = LimelightHelpers.getBotPose_TargetSpace(LimelightConstants.LIMELIGHT_NAME);
    double initialZPos = postions[2]; // negative means in front of
    double initialXPos = postions[0]; // positive means to right
    double initalRotPos = (Math.atan2(-initialXPos, -initialZPos)*180/Math.PI);
    double initialRadius = Math.pow((initialZPos*initialZPos+initialXPos*initialXPos), 0.5);
    double radiusScaleFactor = Constants.LimelightConstants.DesiredRadius/initialRadius;
    double[] data = {initialZPos*radiusScaleFactor, initialXPos*radiusScaleFactor, initalRotPos};
    for ( int i = 0 ; i < 6 ; i++ ) {
      System.out.println(postions[i]);}
    for ( int i = 0 ; i < 3 ; i++ ) {
      System.out.println(data[i]);}
    return data;
  }

  @Override
  public void initialize() {
    double[] data = Computefinalstaticpose();
    // System.out.println(Math.round(data[0]));
    // System.out.println(Math.round(data[1]));
    // System.out.println(Math.round(data[2]));
    this.stopTimer = new Timer();
    this.stopTimer.start();
    this.dontSeeTagTimer = new Timer();
    this.dontSeeTagTimer.start();

    rotController.setSetpoint(data[2]);
    rotController.setTolerance(LimelightConstants.ROT_TOLERANCE_REEF_ALIGNMENT);

    xController.setSetpoint(data[0]);
    xController.setTolerance(LimelightConstants.X_TOLERANCE_REEF_ALIGNMENT);

    yController.setSetpoint(isRightScore ? data[1] : -data[1]);
    yController.setTolerance(LimelightConstants.Y_TOLERANCE_REEF_ALIGNMENT);

    tagID = LimelightHelpers.getFiducialID(LimelightConstants.LIMELIGHT_NAME);
    System.out.println(tagID);
  }

  @Override
  public void execute() {
    if (LimelightHelpers.getTV(LimelightConstants.LIMELIGHT_NAME) && LimelightHelpers.getFiducialID(LimelightConstants.LIMELIGHT_NAME) == tagID) {
      this.dontSeeTagTimer.reset();

      double[] postions = LimelightHelpers.getBotPose_TargetSpace(LimelightConstants.LIMELIGHT_NAME);
      SmartDashboard.putNumber("x", postions[2]);
      double xSpeed = xController.calculate(postions[2]);
      SmartDashboard.putNumber("xspee", xSpeed);
      double ySpeed = -yController.calculate(postions[0]);
      double rotValue = -rotController.calculate(postions[4]);
      drivebase.drive(new Translation2d(xSpeed, ySpeed), rotValue, false);

      if (!rotController.atSetpoint() ||
          !yController.atSetpoint() ||
          !xController.atSetpoint()) {
        stopTimer.reset();
      }
    } else {
      drivebase.drive(new Translation2d(), 0, false);
    }

    SmartDashboard.putNumber("poseValidTimer", stopTimer.get());
  }

  
  @Override
  public void end(boolean interrupted) {
    drivebase.drive(new Translation2d(), 0, false);
  }

  @Override
  public boolean isFinished() {
    // Requires the robot to stay in the correct position for 0.3 seconds, as long as it gets a tag in the camera
    return this.dontSeeTagTimer.hasElapsed(LimelightConstants.DONT_SEE_TAG_WAIT_TIME) ||
        stopTimer.hasElapsed(LimelightConstants.POSE_VALIDATION_TIME);
  }
}
