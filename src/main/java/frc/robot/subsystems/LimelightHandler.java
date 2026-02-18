package frc.robot.subsystems;

import java.util.Optional;

import edu.wpi.first.units.Unit;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.LimelightHelpers.RawFiducial;
import frc.robot.LimelightHelpers;

import javax.swing.JTable;

public class LimelightHandler extends SubsystemBase {
	public RawFiducial[] fiducials;

	public LimelightHandler() {
	}

	public RawFiducial[] getFiducials( ) {
		return LimelightHelpers.getRawFiducials(LimelightConstants.LIMELIGHT_NAME);
	}

	public Optional<RawFiducial> getFiducialByID( int id ) {
		for ( RawFiducial raw : getFiducials() ) {
			if ( raw.id == id ) {
				return Optional.of(raw);
			}
		}

		return Optional.empty();
	}

	@Override
	public void periodic() {
	}

	public InstantCommand printFiducials( ) {
		return new InstantCommand(() -> {
			for ( RawFiducial raw : getFiducials() ) {
				System.out.println("ID: " + raw.id + "\tDistance: " + raw.distToCamera + "\tPosition: (" + raw.txnc + ", " + raw.tync + ")");
			}
			System.out.println("---------------------");
		});
	}

	// public InstantCommand checkLimelight( ) {
	// 	return new InstantCommand(() -> {
	// 		System.out.println("Limelight connected: " + LimelightHelpers.getTV(LimelightConstants.LIMELIGHT_NAME));
	// 	});
	// }
}
