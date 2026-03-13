package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;

import java.util.Optional;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.LimelightHelpers;
import frc.robot.subsystems.LimelightHandler;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.Constants.FieldConstants;
import frc.robot.Constants.LimelightConstants;
import frc.robot.LimelightHelpers.RawFiducial;


public class Movetotag extends Command {
  public PIDController xController, yController, rotController;
  private Timer dontSeeTagTimer, stopTimer;
  private SwerveSubsystem drivebase;
  private double tagID = 15;
  private boolean isHubMode;
  private LimelightHandler LLH = null;
  public double initialXPos, initialYPos, initalRotPose;

  /**
   * @param isRightScore
   * @param drivebase
   */
  public Movetotag(SwerveSubsystem drivebase, boolean isHubMode) {
    xController = new PIDController(LimelightConstants.X_REEF_ALIGNMENT_P, 0.0, 0);  // Vertical movement
    yController = new PIDController(LimelightConstants.Y_REEF_ALIGNMENT_P, 0.0, 0);  // Horitontal movement
    rotController = new PIDController(LimelightConstants.ROT_REEF_ALIGNMENT_P, 0, 0);  // Rotation
    this.drivebase = drivebase;
    this.isHubMode = isHubMode;
    addRequirements(drivebase);
  }

  public Movetotag(SwerveSubsystem drivebase, LimelightHandler LL) {
    this(drivebase, false);
    LLH = LL;
  }

  public double[] Computefinalstaticpose(){
    double[] postions = LimelightHelpers.getBotPose_TargetSpace(LimelightConstants.LIMELIGHT_NAME);
    double initialXPos = postions[0]; // positive means to right
    double initialZPos = Math.abs(postions[2]); // negative means in front of
    double R0 = Constants.LimelightConstants.DesiredRadius;
    double Deltaz = Constants.LimelightConstants.TargetDeltaZ;
    double Zdist = initialZPos + Deltaz;
    double phi = (Math.atan2(-initialXPos, Zdist));
    double newX = -R0*Math.sin(phi);
    double newZ = -(R0*Math.cos(phi) - Deltaz);
    double phi_deg = Math.toDegrees(phi);
    double[] data = {newZ,newX,phi_deg};
    return data;
  }

  @Override
  public void initialize() {

    if (isHubMode) {
      Optional<RawFiducial> m = this.LLH.getHubTag();
      if (m.isPresent()) {
        tagID = m.get().id;
        LimelightHelpers.setPriorityTagID(LimelightConstants.LIMELIGHT_NAME, (int)tagID);
      }
    } else {
      tagID = LimelightHelpers.getFiducialID(LimelightConstants.LIMELIGHT_NAME);
    }

    double[] data = Computefinalstaticpose();
    this.stopTimer = new Timer();
    this.stopTimer.start();
    this.dontSeeTagTimer = new Timer();
    this.dontSeeTagTimer.start();

    rotController.setSetpoint(data[2]);
    rotController.setTolerance(LimelightConstants.ROT_TOLERANCE_REEF_ALIGNMENT);

    xController.setSetpoint(data[0]);
    xController.setTolerance(LimelightConstants.X_TOLERANCE_REEF_ALIGNMENT);

    yController.setSetpoint(data[1]);
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
    LimelightHelpers.setPriorityTagID(LimelightConstants.LIMELIGHT_NAME, -1);
  }

  @Override
  public boolean isFinished() {
    // Requires the robot to stay in the correct position for 0.3 seconds, as long as it gets a tag in the camera
    return this.dontSeeTagTimer.hasElapsed(LimelightConstants.DONT_SEE_TAG_WAIT_TIME) ||
        stopTimer.hasElapsed(LimelightConstants.POSE_VALIDATION_TIME);
  }
}
