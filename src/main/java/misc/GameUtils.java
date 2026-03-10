package misc;

import frc.robot.Constants.LimelightConstants;
import frc.robot.LimelightHelpers;

public class GameUtils {
 public static double[] Computeangleandmove(){
    double[] postions = LimelightHelpers.getBotPose_TargetSpace(LimelightConstants.LIMELIGHT_NAME);
    double initialXPos = postions[0]; // positive means to right
    double initialZPos = Math.abs(postions[2]); // negative means in front of
    double Zdist = initialZPos + LimelightConstants.TargetDeltaZ;
    double phi = (Math.atan2(-initialXPos, Zdist));
    double newX = -LimelightConstants.DesiredRadius*Math.sin(phi);
    double newZ = -(LimelightConstants.DesiredRadius*Math.cos(phi) - LimelightConstants.TargetDeltaZ);
    double phi_deg = phi*180/Math.PI;
    double[] data = {newZ,newX,phi_deg};
    return data;
  }

  // slimmed down version of Computeangleandmove() that just returns angle
  public static double getHubAngle() {
    double[] postions = LimelightHelpers.getBotPose_TargetSpace(LimelightConstants.LIMELIGHT_NAME);
    double initialXPos = postions[0]; // positive means to right
    double Zdist = Math.abs(postions[2])+ LimelightConstants.TargetDeltaZ;
    double phi = (Math.atan2(-initialXPos, Zdist));
    return phi*180/Math.PI;
  }
}
