// Sugerman:
// ChatGPT rewrite of Movetotag.java to remove translation and only do rotation. 
// It said there were some mistakes in the code and cleaned them up?

package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.LimelightHelpers;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

public class Movetotag extends Command {

  private PIDController rotController;
  private Timer dontSeeTagTimer;
  private SwerveSubsystem drivebase;

  private double tagID = 26;

  public Movetotag(SwerveSubsystem drivebase) {
    this.drivebase = drivebase;

    rotController = new PIDController(
        Constants.LimelightConstants.ROT_REEF_ALIGNMENT_P, 0, 0);

    // IMPORTANT: prevents long-way spinning
    rotController.enableContinuousInput(-Math.PI, Math.PI);

    addRequirements(drivebase);
  }

  @Override
  public void initialize() {
    dontSeeTagTimer = new Timer();
    dontSeeTagTimer.start();

    rotController.setTolerance(
        Math.toRadians(Constants.LimelightConstants.ROT_TOLERANCE_REEF_ALIGNMENT));
  }

  @Override
  public void execute() {

    if (LimelightHelpers.getTV(Constants.LimelightConstants.LIMELIGHT_NAME)
        && LimelightHelpers.getFiducialID(Constants.LimelightConstants.LIMELIGHT_NAME) == tagID) {

      dontSeeTagTimer.reset();

      double[] pose = LimelightHelpers.getBotPose_TargetSpace(
          Constants.LimelightConstants.LIMELIGHT_NAME);

      double robotX = pose[0];
      double robotZ = pose[2];

      // ✅ Target point: behind tag
      double targetX = 0.0;
      double targetZ = Constants.LimelightConstants.TargetDeltaZ;

      double dx = targetX - robotX;
      double dz = targetZ - robotZ;

      // ✅ Correct yaw calculation for Limelight target space
      double desiredYaw = Math.atan2(dx, dz);

      // ✅ FIXED: yaw is index 5 (degrees → radians)
      double currentYaw = Math.toRadians(pose[4]);

      double rotOutput = rotController.calculate(currentYaw, desiredYaw);

      // Optional deadband
      if (Math.abs(rotOutput) < 0.05) {
        rotOutput = 0;
      }

      // 🚫 NO TRANSLATION
      drivebase.drive(new edu.wpi.first.math.geometry.Translation2d(0, 0),
          -rotOutput,  // may flip sign depending on your drivetrain
          false);

    } else {
      drivebase.drive(new edu.wpi.first.math.geometry.Translation2d(), 0, false);
    }
  }

  @Override
  public void end(boolean interrupted) {
    drivebase.drive(new edu.wpi.first.math.geometry.Translation2d(), 0, false);
  }

  @Override
  public boolean isFinished() {
    return dontSeeTagTimer.hasElapsed(
        Constants.LimelightConstants.DONT_SEE_TAG_WAIT_TIME);
  }
}
