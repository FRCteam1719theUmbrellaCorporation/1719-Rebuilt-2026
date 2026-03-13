package frc.robot;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.Constants.OutakeConstants;
import java.util.Map;

/** Shared Shuffleboard entries for motion-compensation settings. */
public final class MotionCompDashboard {
    private MotionCompDashboard() {}

    /** Toggle switch: enable/disable motion compensation. Defaults OFF — no effect when off. */
    public static final GenericEntry ENABLED =
        Shuffleboard.getTab("Aiming")
            .add("Motion Comp Enabled", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();

    /**
     * Ball speed (m/s) at 100% shooter power.
     * Time of flight = d / (ScailPower(d) * this value).
     * Calibrate: fire at known distance d, time with slow-mo video, then
     *   BALL_SPEED_AT_FULL_POWER = d / (t * ScailPower(d)).
     */
    public static final GenericEntry BALL_SPEED_AT_FULL_POWER =
        Shuffleboard.getTab("Aiming")
            .add("Ball Speed @ Full Power (m/s)", OutakeConstants.BALL_SPEED_AT_FULL_POWER)
            .withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", 1.0, "max", 30.0))
            .getEntry();
}
