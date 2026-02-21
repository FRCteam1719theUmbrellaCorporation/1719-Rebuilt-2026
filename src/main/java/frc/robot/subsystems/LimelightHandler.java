package frc.robot.subsystems;

import java.lang.StackWalker.Option;
import java.util.Optional;

import javax.swing.border.Border;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.Unit;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.Constants.LimelightConstants.TeamColor;
import frc.robot.LimelightHelpers.PoseEstimate;
import frc.robot.LimelightHelpers.RawFiducial;
import misc.Table;
import misc.Table.Borders;
import misc.Table.Style;
import misc.Angle;
import frc.robot.LimelightHelpers;

public class LimelightHandler extends SubsystemBase {
	public RawFiducial[] fiducials;

	public LimelightHandler() {
	}

	public RawFiducial[] getFiducials( ) {
		return LimelightHelpers.getRawFiducials(LimelightConstants.LIMELIGHT_NAME);
	}

	public Optional<RawFiducial> getFiducialByID( int tagID ) {
		for ( RawFiducial raw : getFiducials() ) {
			if ( raw.id == tagID ) {
				return Optional.of(raw);
			}
		}

		return Optional.empty();
	}

	public Optional<Double> getDistFromTag( int tagID ) {
		Optional<RawFiducial> tag = this.getFiducialByID(tagID);
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

	// public Optional<Double[]> getAngleFromTag( int tagID ) {
	// 	Optional<Double> dist = this.getDistFromTag(tagID);
	// 	if (dist.isEmpty()) { return Optional.empty(); }

	// 	if (dist.get() < LimelightConstants.MAX_TAG_DIST){
	// 	}
	// }

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
	}
}
