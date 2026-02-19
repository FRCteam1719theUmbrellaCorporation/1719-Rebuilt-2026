package frc.robot.subsystems;

import java.util.Optional;

import edu.wpi.first.units.Unit;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.LimelightHelpers.RawFiducial;
import misc.Table;
import frc.robot.LimelightHelpers;

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
			Table T = new Table("ID", "Distance", "TXNC", "TYNC", "Area", "Ambiguity")
				.borders(Table.Borders.HEAD.id | Table.Borders.BOTTOM_INNER.id)
				.style(Table.Style.SOLID);
			for ( RawFiducial raw : getFiducials() ) {
				T.addRow(raw.id, raw.distToCamera, raw.txnc, raw.tync, raw.ta, raw.ambiguity);
			}
			T.print();
		});
	}

	// public InstantCommand checkLimelight( ) {
	// 	return new InstantCommand(() -> {
	// 		System.out.println("Limelight connected: " + LimelightHelpers.getTV(LimelightConstants.LIMELIGHT_NAME));
	// 	});
	// }
}
