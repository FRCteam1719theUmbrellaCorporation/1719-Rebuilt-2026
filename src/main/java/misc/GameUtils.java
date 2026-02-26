package misc;

import java.util.Optional;

import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.Constants.FieldConstants;


public class GameUtils {
    private static Optional<Alliance> m_Alliance;

    public GameUtils() {
        m_Alliance = DriverStation.getAlliance(); // only needs to be ran once
    }

    // def not needed but just incase
    public static Optional<Alliance> UpdateAlliance() {
        return DriverStation.getAlliance();
    }

    /**
     * Gets the tag the robot should target for outake 
     * based on its alliance color
     * 
     * throws an IllegalStateException if there is no Team initiated
     * as this is supposed to be called when a team is already defined
     */
    public static int GetHubTag_Alliance() throws IllegalStateException {
        Alliance alliance = m_Alliance.orElseThrow(IllegalStateException::new); 
        return alliance == Alliance.Red 
            ? FieldConstants.SHOOTER_APRIL_TAG_RED 
            : FieldConstants.SHOOTER_APRIL_TAG_BLUE;
    }
}
