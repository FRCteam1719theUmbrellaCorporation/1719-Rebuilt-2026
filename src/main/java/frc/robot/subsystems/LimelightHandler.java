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
	public Optional<AprilTagFiducial> getAprilTag( int id, int limelight ) {
		limelight = 1;

		// if (limelight == -1 || limelight == 0) {
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

		if (limelight == 1 || limelight == 0) {
			Optional<LimelightResults> results = this
			.limelightFront
			.getLatestResults();
			if (results.isPresent()) {
				for (AprilTagFiducial tag : results.get().targets_Fiducials) {
					if (tag.fiducialID == id) {
						return Optional.of(tag);
					}
				}
			}
		}

		return Optional.empty();
	}

	public InstantCommand logLimelightExists( int id ) {
		System.out.println("calling check thing idk");
		return new InstantCommand(() -> {
			System.out.print("April tag with ID ");
			System.out.print(id);
			Optional<AprilTagFiducial> tag = this.getAprilTag(id, 11);
			if (tag.isPresent()) {
				System.out.print("was found.");
			} else {
				System.out.print("was \u001B[1mnot\u001B[m found.");
			}
		});
	}
}
