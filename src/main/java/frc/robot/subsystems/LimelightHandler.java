package frc.robot.subsystems;

import java.util.Optional;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
//import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.FieldConstants;
import frc.robot.Constants.LimelightConstants;
import frc.robot.LimelightHelpers.PoseEstimate;
import frc.robot.LimelightHelpers.RawFiducial;
// import misc.Table;
// import misc.Table.Borders;
import frc.robot.LimelightHelpers;

public class LimelightHandler extends SubsystemBase {
	public RawFiducial[] fiducials;
	private final Supplier<Rotation2d> robotHeading;
	private final StructPublisher<Pose2d> posePublisher;
	private final BooleanPublisher poseValidPublisher;
	private final DoublePublisher tagCountPublisher;
	private final DoublePublisher latencyPublisher;
	private final DoublePublisher averageTagDistancePublisher;
	private Pose2d lastValidPose = new Pose2d();

	public LimelightHandler(Supplier<Rotation2d> robotHeading) {
		this.robotHeading = robotHeading;
		var nt = NetworkTableInstance.getDefault();
		posePublisher = nt.getStructTopic("Vision/LimelightPose", Pose2d.struct).publish();
		poseValidPublisher = nt.getBooleanTopic("Vision/LimelightPoseValid").publish();
		tagCountPublisher = nt.getDoubleTopic("Vision/LimelightTagCount").publish();
		latencyPublisher = nt.getDoubleTopic("Vision/LimelightLatencyMs").publish();
		averageTagDistancePublisher = nt.getDoubleTopic("Vision/LimelightAverageTagDistanceMeters").publish();
		posePublisher.set(lastValidPose);
		poseValidPublisher.set(false);
		tagCountPublisher.set(0);
		latencyPublisher.set(0);
		averageTagDistancePublisher.set(0);
	}

	public RawFiducial[] getFiducials( ) {
		return LimelightHelpers.getRawFiducials(LimelightConstants.LIMELIGHT_NAME);
	}

	public Optional<RawFiducial> getFiducialByID(int tagID) {
		for (RawFiducial f : fiducials) {  // use cached field, not a fresh NT call
			if (f.id == tagID) return Optional.of(f);
		}
		return Optional.empty();
	}

	public boolean seesTargetTag(int tagId) {
		return getFiducialByID(tagId).isPresent();
	}

	public boolean seesHubTag() {
		return getHubTag().isPresent();
	}

	public double getBotRadius() {
		return getHubTag()
			.map(f -> f.distToRobot)
			.orElse(-1.0);
	}

	public Optional<RawFiducial> getHubTag() {
		Optional<RawFiducial> tag = Optional.empty();
		for (RawFiducial f : fiducials) {  // use cached field, not a fresh NT call
			for (int ht : FieldConstants.HUBTAGS)
			if (f.id == ht) {
				tag = Optional.of(f);
				break;
			}
		}

		return tag;
	}


	public Optional<Double> getDistFromTag( int tagID ) {
		Optional<RawFiducial> tag = this.getFiducialByID(tagID);
		if (tag.isPresent()) {
			return Optional.of(tag.get().distToRobot);
		} else {
			return Optional.empty();
		}
	}

	public Optional<Double> getDistFromHub() {
		Optional<RawFiducial> tag = this.getHubTag();
		if (tag.isPresent()) {
			return Optional.of(tag.get().distToRobot);
		} else {
			return Optional.empty();
		}
	}

	public Optional<Double> getAngleFromTag( int tagID ) {
		Optional<RawFiducial> tag = this.getFiducialByID(tagID);
		if (tag.isPresent()) {
			return Optional.of(tag.get().txnc);
		} else {
			return Optional.empty();
		}
	}

	public Optional<Double> getAngleFromHub() {
		Optional<RawFiducial> tag = getHubTag();

		if (tag.isPresent()) {
			return Optional.of(tag.get().txnc);
		} else {
			return Optional.empty();
		}
	}

	// public static Optional<Boolean> isBlueAllianceTag(Optional<RawFiducial> tag) {
	// 	if (tag.isEmpty()) return Optional.empty();
	// 	return Optional.of(tag.get().id == FieldConstants.HUBID_RED);
	// }

	// public static Optional<Boolean> isBlueAllianceTag(int tagID) {
	// 	return Optional.of(tagID == FieldConstants.HUBID_RED);
	// }

	private PoseEstimate getBotPoseEstimate( ) {
		if (LimelightConstants.USE_MEGATAG2) {
			return LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(LimelightConstants.LIMELIGHT_NAME);
		} else {
			return LimelightHelpers.getBotPoseEstimate_wpiBlue(LimelightConstants.LIMELIGHT_NAME);
		}
	}

	private Pose2d getBotPose( ) {
		PoseEstimate estimate = this.getBotPoseEstimate();
		return estimate.pose;
	}

	// private Angle getBotHeading( ) {
	// 	return Angle.rotations(
	// 		getBotPose()
	// 		.getRotation()
	// 		.getRotations()
	// 	);
	// }
	
	public double[] getBotPosition( ) {
		Pose2d pose = this.getBotPose();
		return new double[]{
			pose.getX(),
			pose.getY(),
		};
	}

	// public InstantComman printPose( ) {
	// 	return new InstantCommand(() -> {
	// 		Table T = new Table("ID", "x", "y", "z", "roll", "pitch", "yaw")
	// 			.borders(Borders.BODY)
	// 			.style(Style.SOLID);

	// 		for (RawFiducial tag : this.getFiducials()) {
				
	// 		}
	// 	});
	// }

	// public InstantCommand printFiducials( ) {
	// 	return new InstantCommand(() -> {
	// 		Table T = new Table("ID", "Distance", "TXNC", "TYNC", "Area", "Ambiguity")
	// 			.borders(Table.Borders.HEAD.id | Table.Borders.BOTTOM_INNER.id)
	// 			.style(Table.Style.SOLID);
	// 		for ( RawFiducial raw : getFiducials() ) {
	// 			T.addRow(raw.id, raw.distToCamera, raw.txnc, raw.tync, raw.ta, raw.ambiguity);
	// 		}
	// 		T.print();
	// 	});
	// }

	// public InstantCommand printAngles( ) {
	// 	return new InstantCommand(() -> {
	// 		Table T = new Table("ID", "Distance", "Angle of Tag", "Bot Heading (degrees)")
	// 			.borders(Borders.ALL);
	// 		for (RawFiducial raw : getFiducials()) {
	// 			T.addRow(raw.id, raw.distToCamera, getAngleFromTag(raw.id), getBotHeading().degrees);
	// 		}
	// 		T.print();
	// 	});
	// }

@Override
public void periodic() {
		// MegaTag2 needs the robot's field-relative yaw. Send the current odometry
		// heading before reading the MT2 estimate.
		LimelightHelpers.SetRobotOrientation_NoFlush(
			LimelightConstants.LIMELIGHT_NAME,
			robotHeading.get().getDegrees(),
			0, 0, 0, 0, 0);

		PoseEstimate estimate = getBotPoseEstimate();
		boolean poseValid = estimate != null && estimate.tagCount > 0;
		poseValidPublisher.set(poseValid);
		tagCountPublisher.set(poseValid ? estimate.tagCount : 0);
		latencyPublisher.set(poseValid ? estimate.latency : 0);
		averageTagDistancePublisher.set(poseValid ? estimate.avgTagDist : 0);

		// Keep the last valid pose on the field while marking the current sample
		// invalid separately. This avoids plotting Limelight's zero pose on tag loss.
		if (poseValid) {
			lastValidPose = estimate.pose;
		}
		posePublisher.set(lastValidPose);

    fiducials = LimelightHelpers.getRawFiducials(LimelightConstants.LIMELIGHT_NAME);
}
}
