// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.trajectory.constraint.MaxVelocityConstraint;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Time;
import swervelib.math.Matter;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean constants. This
 * class should not be used for any other purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants
{

  public static final double ROBOT_MASS = (100) * 0.453592; // 105lbs * kg per pound
  public static final Matter CHASSIS    = new Matter(new Translation3d(0, 0, Units.inchesToMeters(8)), ROBOT_MASS);
  public static final double LOOP_TIME  = 0.13; //s, 20ms + 110ms sprk max velocity lag
  public static final double MAX_SPEED_MULTIPLIER = 0.80;
  public static final double MAX_SPEED  = MAX_SPEED_MULTIPLIER*Units.feetToMeters(14.5);

  public static final double Motor_Min = -1;
  public static final double Motor_Max = 1;
  // Maximum speed of the robot in meters per second, used to limit acceleration.

//  public static final class AutonConstants
//  {
//
//    public static final PIDConstants TRANSLATION_PID = new PIDConstants(0.7, 0, 0);
//    public static final PIDConstants ANGLE_PID       = new PIDConstants(0.4, 0, 0.01);
//  }

  public static final class DrivebaseConstants
  {

    // Hold time on motor brakes when disabled
    public static final double WHEEL_LOCK_TIME = 10; // seconds
  }

  public static class OperatorConstants
  {

    // Joystick Deadband
    public static final double DEADBAND        = 0.1;
    public static final double LEFT_Y_DEADBAND = 0.1;
    public static final double RIGHT_X_DEADBAND = 0.1;
    public static final double TURN_CONSTANT    = 6;

    public static final double JOYSTICK_SENSITIVITY_FACTOR = 3.f;
    public static final double EPISLON = 1.e-16;

    public static final double SlowDriveFactor = 0.4f; 
    public static final double SlowDriverRadius = 0.2f;
    public static final double SlowDriverMin = SlowDriveFactor - SlowDriverRadius;
    public static final double SlowDriverMax = SlowDriveFactor + SlowDriverRadius;
    public static final double NormalDriveFactor = 1f; 
    public static final double SHAKE_POWER_X = 1.d;
  }

  public static final class HapticConstants {
    public static final int HUB_TAG_ID = 3;
    public static final double[] HUB_VIBRATE_DISTANCE = {
      1.524,
      1.8288,
    };
    public static final double HUB_DIST_VIBRATE_STRENGTH = .2;
	  public static final double HUB_SEE_VIBRATE_STRENGTH = 0.5;
  }

  public static final class LimelightConstants {
		public static final String LIMELIGHT_NAME = null;
    public static final Double MAX_TAG_DIST = 10.0;
    public static final TeamColor TEAM = TeamColor.RED;
    public static final boolean USE_MEGATAG2 = true;

    public static enum TeamColor {
      RED,
      BLUE,
    }
//these two P values need to be tuned
    public static final double X_REEF_ALIGNMENT_P =  3.3;
    public static final double Y_REEF_ALIGNMENT_P =  3.3;
    public static final double ROT_REEF_ALIGNMENT_P = 0.058;
    public static final double ROT_TOLERANCE_REEF_ALIGNMENT = 10;
    public static final double X_TOLERANCE_REEF_ALIGNMENT = 0.50;
    public static final double Y_TOLERANCE_REEF_ALIGNMENT = 0.50;


    public static final double PositionScalar = 3.0;
    public static final double ROT_SETPOINT_REEF_ALIGNMENT = 0;  // Rotation
    //xsetpoint was originally -0.34
    public static final double X_SETPOINT_REEF_ALIGNMENT = -0.5*PositionScalar;  // Vertical pose
    //ysetpoint was originally 0.16
    public static final double Y_SETPOINT_REEF_ALIGNMENT = 0*PositionScalar;  // Horizontal pose
    public static final double DONT_SEE_TAG_WAIT_TIME = 1;
	  public static final double POSE_VALIDATION_TIME = 0.3;

    public static final double DesiredRadius = 2.65;
    public static final double TargetDeltaZ =  0.61; // 2 feet

    public static final double AIM_AT_TAG_TOLERANCE = 3.d;
	}


  public static final class IntakeConstants {
    public static final int ID = 3;
    public static final float INTAKE_SPEED = 0.9f;
    //TODO: impl important vars
	public static final double REV_INTAKE_TIME = .4; // measured in seconds babbyyyyy
  }
  
  public static final class OutakeConstants{
    public static final int FUNNEL_ID = 4;
    public static final int SHOOTER_ID = 6;
    public static final int BLENDER_ID = 7;

    public static final float OUTAKE_SPEED = 0.7f;
    public static final float FUNNEL_SPEED = 0.4f;
    public static final float Slow_OUTAKE_SPEED = 0.3f;
    public static final float Super_OUTAKE_SPEED = 1;

    public static final float BlenderSpeed = -.5f;

    public static final float OUTAKE_TIME = 0.4f;

    public static final double MinShootDistance = .9;
    //this was lowered from 0.085
    public static final double DistancePowerMult = 0.0830589;
    public static final double DistancePowerOffset = 0.507221;
    public static final double ShooterScailTimeout = 2.d;
    public static final double BloaderVel = 200;           
    public static final double PULSE_TIME = 2.0d;
    public static final double PULSE_BACK_TIME = 1.d;
  }

  public static final class ControllerConstants {
    public static final double TrimSwitchBounds = .2d;
    public static final double TrimSwitchLow = 1-TrimSwitchBounds;
    public static final double TrimSwitchHigh = 1+TrimSwitchBounds;
    
  }

  public static final class FieldConstants {
    public static final int HUBID_RED = 10;
    public static final int HUBID_BLUE = 25;

    public static final int[] HUBTAGS = {5,10,2,21,26,18};
  }
}
