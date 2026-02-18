package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import limelight.Limelight;
import limelight.networktables.LimelightResults;
import limelight.networktables.target.AprilTagFiducial;
import frc.robot.Constants.LimelightConstants;
import frc.robot.Constants.LimelightConstants.LimelightSide;

public class LimelightHandler extends SubsystemBase {
	public Limelight limelightFront;
	// public Limelight limelightBack;

	public Optional<LimelightResults> limelightFrontResults;
	// public Optional<LimelightResults> limelightBackResults;

	public AprilTagFiducial[] frontAprilTags;
	// public AprilTagFiducial[] backAprilTags;
	
	public LimelightHandler() {
		this.limelightFront = new Limelight(LimelightConstants.limelightFrontName);
		// this.limelightBack = new Limelight(LimelightConstants.limelightBackName);
	}

	@Override
	public void periodic() {
		// Get results
		this.limelightFrontResults = this.limelightFront.getLatestResults();
		// this.limelightBackResults = this.limelightBack.getLatestResults();
		
		// Get tags
		this.limelightFrontResults.ifPresent((LimelightResults res) -> {
			this.frontAprilTags = res.targets_Fiducials;
		});
		// this.limelightBackResults.ifPresent((LimelightResults res) -> {
		// 	this.backAprilTags = res.targets_Fiducials;
		// });
	}

	/**
	 * @param id ID of the april tag we're looking for
	 * @param limelight Which limelight to scan: 0 for any, 1 for front, -1 for back
	 * @return Optional april tag if its on screen
	 */
	public Optional<AprilTagFiducial> getAprilTag( int id, LimelightSide limelight ) {
		// if (limelight == LimelightSide.BACK || limelight == LimelightSide.BOTH) {
		// 	Optional<LimelightResults> results = this
		// 	.limelightBack
		// 	.getLatestResults();
		// 	if (results.isPresent()) {
		// 		for (AprilTagFiducial tag : results.get().targets_Fiducials) {
		// 			if (tag.fiducialID == id) {
		// 				return Optional.of(tag);
		// 			}
		// 		}
		// 	}
		// }

		if (limelight == LimelightSide.FRONT || limelight == LimelightSide.BOTH) {
			Optional<LimelightResults> results = this
				.limelightFront
				.getLatestResults();
			if (results.isPresent()) {
				System.out.println("is present");
				for (AprilTagFiducial tag : results.get().targets_Fiducials) {
					System.out.println("Checking april tag " + tag.fiducialID);
					if (tag.fiducialID == id) {
						return Optional.of(tag);
					}
				}
			}
		}

		return Optional.empty();
	}

	public InstantCommand logLimelightExists( int id ) {
		System.out.println(LimelightHelpers.getTV(LimelightConstants.limelightFrontName));
		return new InstantCommand(() -> {
			String msg = "April tag with ID `" + id;
			Optional<AprilTagFiducial> tag = this.getAprilTag(id, LimelightSide.BOTH);
			if (tag.isPresent()) {
				msg += "` was found.";
			} else {
				msg += "` was not found.";
			}
			System.out.println(msg);
		});
	}
}
