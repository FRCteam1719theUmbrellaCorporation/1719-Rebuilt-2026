// // Made by ChatGPT with the prompt:
// //
// // I am using WPILib and LimeLight MegaTag2. I need a code during
// // teleop that I can execute with a controller's trigger button. If
// // the camera sees April Tag 15, it will use getBotPose_TargetSpace to
// // measure the robots position and yaw from the tag. It will compute
// // the new yaw to a point 60 cm directly behind the tag. Then it will
// // use a PID swerve drive command to rotate the robot to that new yaw.

// // This manually sets P,I,D as 4,0,2 and only points to tag 26.

// // BS: Blindly grabbed these from AimAtTag.java
// package frc.robot.commands;

// import java.util.Optional;
// import edu.wpi.first.math.MathUtil;
// import edu.wpi.first.math.controller.PIDController;
// import edu.wpi.first.math.geometry.Translation2d;
// import edu.wpi.first.wpilibj.Timer;
// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
// import frc.robot.Constants;
// import frc.robot.Constants.LimelightConstants;
// import frc.robot.subsystems.LimelightHandler;
// import frc.robot.subsystems.swervedrive.SwerveSubsystem;
// // End blind import

// public class TurnToHub extends Command {
//     private final SwerveSubsystem swerve;
//     private final XboxController controller;

//     private final PIDController rotPID = new PIDController(4.0, 0.0, 0.2);

//     public TurnToHub(SwerveSubsystem swerve, XboxController controller) {
//         this.swerve = swerve;
//         this.controller = controller;

//         rotPID.enableContinuousInput(-Math.PI, Math.PI);
//         addRequirements(swerve);
//     }

//     @Override
//     public void execute() {
//         // Only run while trigger is held
//         if (controller.getRightTriggerAxis() < 0.5) {
//             swerve.drive(0, 0, 0, true);
//             return;
//         }

//         // Check if tag 26 is visible
//         double tv = LimelightHelpers.getTV("");
//         double tid = LimelightHelpers.getFiducialID("");

//         if (tv == 0 || tid != 26) {
//             swerve.drive(0, 0, 0, true);
//             return;
//         }

//         // Get robot pose in target space
//         double[] pose = LimelightHelpers.getBotPose_TargetSpace("");

//         double robotX = pose[0]; // meters
//         double robotZ = pose[2];

//         // Target point: 0.6m behind tag
//         double targetX = 0.0;
//         double targetZ = -0.6;

//         // Compute vector from robot to target
//         double dx = targetX - robotX;
//         double dz = targetZ - robotZ;

//         // Desired yaw (in radians)
//         double desiredYaw = Math.atan2(dx, dz);

//         // Current robot yaw relative to tag
//         double currentYaw = Math.toRadians(pose[4]); // yaw in degrees -> radians

//         // PID output
//         double rotOutput = rotPID.calculate(currentYaw, desiredYaw);

//         // Drive: no translation, only rotation
//         swerve.drive(0.0, 0.0, rotOutput, true);
//     }

//     @Override
//     public void end(boolean interrupted) {
//         swerve.drive(0, 0, 0, true);
//     }
// }
