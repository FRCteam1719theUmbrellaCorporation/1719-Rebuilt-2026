package frc.robot.subsystems;

import java.util.Optional;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.FieldConstants;
import frc.robot.Constants.LimelightConstants;
import frc.robot.LimelightHelpers.PoseEstimate;
import frc.robot.LimelightHelpers.RawFiducial;
import misc.Table;
import misc.Table.Borders;
import misc.Angle;
import frc.robot.LimelightHelpers;

public class LimelightHandler extends SubsystemBase {
	public RawFiducial[] fiducials;

	public LimelightHandler() {
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
			if (f.id == FieldConstants.HUBID_RED || f.id == FieldConstants.HUBID_BLUE) {
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

	public static Optional<Boolean> isBlueAllianceTag(Optional<RawFiducial> tag) {
		if (tag.isEmpty()) return Optional.empty();
		return Optional.of(tag.get().id == FieldConstants.HUBID_RED);
	}

	public static Optional<Boolean> isBlueAllianceTag(int tagID) {
		return Optional.of(tagID == FieldConstants.HUBID_RED);
	}

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

	private Angle getBotHeading( ) {
		return Angle.rotations(
			getBotPose()
			.getRotation()
			.getRotations()
		);
	}
	
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

	public InstantCommand printAngles( ) {
		return new InstantCommand(() -> {
			Table T = new Table("ID", "Distance", "Angle of Tag", "Bot Heading (degrees)")
				.borders(Borders.ALL);
			for (RawFiducial raw : getFiducials()) {
				T.addRow(raw.id, raw.distToCamera, getAngleFromTag(raw.id), getBotHeading().degrees);
			}
			T.print();
		});
	}

@Override
public void periodic() {
    fiducials = LimelightHelpers.getRawFiducials(LimelightConstants.LIMELIGHT_NAME);
}
}
