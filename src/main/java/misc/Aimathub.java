package misc;

import frc.robot.Constants;
import frc.robot.Constants.LimelightConstants;
import frc.robot.LimelightHelpers;

public class Aimathub {
 public static double[] Computeangleandmove(){
    double[] postions = LimelightHelpers.getBotPose_TargetSpace(LimelightConstants.LIMELIGHT_NAME);
    double initialXPos = postions[0]; // positive means to right
    double initialZPos = Math.abs(postions[2]); // negative means in front of
    double R0 = Constants.LimelightConstants.DesiredRadius;
    double Deltaz = Constants.LimelightConstants.TargetDeltaZ;
    double Zdist = initialZPos + Deltaz;
    double phi = (Math.atan2(-initialXPos, Zdist));
    double newX = -R0*Math.sin(phi);
    double newZ = -(R0*Math.cos(phi) - Deltaz);
    double phi_deg = phi*180/Math.PI;
    double[] data = {newZ,newX,phi_deg};
    return data;
  }
}
