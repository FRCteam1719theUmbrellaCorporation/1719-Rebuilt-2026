// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.OutakeConstants;
import frc.robot.commands.AimAtTag;
import frc.robot.commands.AimAtTagAuto;
import frc.robot.commands.Movetotag;
import frc.robot.commands.DeviceCommands.BlenderBackPulseCommand;
import frc.robot.commands.DeviceCommands.BlenderPulseCommand;
import frc.robot.commands.DeviceCommands.BriefReverseIntake;
import frc.robot.commands.DeviceCommands.ShootWithDistance;
import frc.robot.commands.swervedrive.SwerveShakeRelative;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.subsystems.LimelightHandler;
import frc.robot.subsystems.devices.BlenderSubsystem;
import frc.robot.subsystems.devices.IntakeSubsystem;
import frc.robot.subsystems.devices.OutakeSubsystem;

import java.io.File;

import swervelib.SwerveInputStream;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer
{

  protected double i_scalar(double coordToReturn, double otherCoord) {
    final double r = Math.sqrt(Math.pow(coordToReturn, 2) 
                             + Math.pow(otherCoord, 2));
    final double scale_factor = Math.pow(
        MathUtil.clamp(r,0,1),
        Constants.OperatorConstants.JOYSTICK_SENSITIVITY_FACTOR);
    return (coordToReturn*scale_factor)/(r+Constants.OperatorConstants.EPISLON);
  }

  // Replace with CommandPS4Controller or CommandJoystick if needed
  final         CommandXboxController driverXbox = new CommandXboxController(0);
  final         CommandXboxController operatorXbox = new CommandXboxController(1);
  final IntakeSubsystem INTAKE = new IntakeSubsystem();
  final OutakeSubsystem OUTAKE = new OutakeSubsystem();
  final BlenderSubsystem BLENDER = new BlenderSubsystem();
  // The robot's subsystems and commands are defined here...
  public final SwerveSubsystem       drivebase  = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(),
                                                                                "swerve/Turbo"));
  private final LimelightHandler LLHandler = new LimelightHandler();

  // // Establish a Sendable Chooser that will be able to be sent to the SmartDashboard, allowing selection of desired auto
  // private final SendableChooser<Command> autoChooser = new SendableChooser<>();
  private final SendableChooser<Command> autoChooser;
  // /**
  //  * Converts driver input into a field-relative ChassisSpeeds that is controlled by angular velocity.
  //  */
  // SwerveInputStream driveAngularVelocity = SwerveInputStream.of(drivebase.getSwerveDrive(),
  //                                                               () -> i_scalar(driverXbox.getLeftY(), driverXbox.getLeftX() * -1),
  //                                                               () -> i_scalar(driverXbox.getLeftX(), driverXbox.getLeftY()) *-1)
  //                                                           .withControllerRotationAxis(()->Math.pow(driverXbox.getRightX(),3)*-1)
  //                                                           .deadband(OperatorConstants.DEADBAND)
  //                                                           .scaleTranslation(0.9)
  //                                                           .allianceRelativeControl(true);

  SwerveInputStream driveAngularVelocity = SwerveInputStream.of(drivebase.getSwerveDrive(),
                                                                () -> i_scalar(driverXbox.getLeftY(),driverXbox.getLeftX()),
                                                                () -> i_scalar(driverXbox.getLeftX(),driverXbox.getLeftY()))
                                                            .withControllerRotationAxis(()->Math.pow(driverXbox.getRightX(),3)*-1)
                                                            .deadband(OperatorConstants.DEADBAND)
                                                            .scaleTranslation(0.9)
                                                            .allianceRelativeControl(true);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  // Named Commands //

  // private GenericEntry matchTime = null;

  public Command CenterWheels = drivebase.centerModulesCommand().withTimeout(0.5);
  public static volatile BriefReverseIntake BRI_Cancel_Ptr;

  public Command StopIntake = new InstantCommand(() -> {
    INTAKE.setSpeed(0);
  });

  public Command Intake = new InstantCommand(() -> {
    INTAKE.setSpeed(IntakeConstants.INTAKE_SPEED);
  });

  public Command ReverseIntake = new InstantCommand(() -> {
    INTAKE.setSpeed(-IntakeConstants.INTAKE_SPEED);
  }).withTimeout(0.5);

  public Command StopShoot = new InstantCommand(() -> {
    OUTAKE.stop();
    BLENDER.setBlenderSpeed(0);
  });

  public Command AimAtTag = new AimAtTagAuto(drivebase, LLHandler).withTimeout(0.5);
  public Command startBlender = new InstantCommand(()->BLENDER.setBlenderSpeed(OutakeConstants.BlenderSpeed));
  public Command ShootRelativeDistance = new SequentialCommandGroup(
    new ShootWithDistance(OUTAKE, LLHandler)).withTimeout(7);
  public Command Shootslow = new InstantCommand(() -> {
    OUTAKE.startShooter();
    OUTAKE.ConstantShoot(0.4f);
  });

  public Command Shootfast = new InstantCommand(() -> {
    OUTAKE.startShooter();
    OUTAKE.ConstantShoot(0.9f);
  });


  public Command Center_wheels = drivebase.centerModulesCommand().withTimeout(0.5);
  public Command AimAtTagAuto = new frc.robot.commands.AimAtTagAuto(drivebase, LLHandler).withTimeout(2);

  public Command BlenderPulse = new BlenderPulseCommand(BLENDER).withTimeout(7);

  public Command BlenderBackPulse = new BlenderBackPulseCommand(BLENDER).withTimeout(7);

  public Command BlendShoot = new ParallelCommandGroup(
    BlenderPulse,
    ShootRelativeDistance
  ).withTimeout(7); //correct maybe?

  public RobotContainer()
  {
    // // Configure the trigger bindings
    configureBindings();
    DriverStation.silenceJoystickConnectionWarning(true);
    BRI_Cancel_Ptr = null;

    /// Registering ///
    NamedCommands.registerCommand("center", CenterWheels);
    NamedCommands.registerCommand("intake", Intake);
    NamedCommands.registerCommand("reverse-intake", ReverseIntake);
    NamedCommands.registerCommand("stop-intake", StopIntake);
    NamedCommands.registerCommand("stop_intake", StopIntake); //somewhere there is a call of stop_intake instead of stop-intake, this is a patchwork fix
    NamedCommands.registerCommand("shoot-relative", ShootRelativeDistance);
    NamedCommands.registerCommand("stop-shooting", StopShoot);
    NamedCommands.registerCommand("AimAtTag", AimAtTag);
    NamedCommands.registerCommand("shoot-slow", Shootslow);
    NamedCommands.registerCommand("shoot-fast", Shootfast);
    NamedCommands.registerCommand("pulse-blender", BlenderPulse);
    NamedCommands.registerCommand("pulse-back", BlenderBackPulse);
    NamedCommands.registerCommand("blend-shoot", BlendShoot);
    
    // //Set the default auto (do nothing) 
    // autoChooser.setDefaultOption("Do Nothing", Commands.none());
    autoChooser = AutoBuilder.buildAutoChooser();

    //Add a simple auto option to have the robot drive forward for 1 second then stop
    // autoChooser.addOption("Drive Forward", drivebase.driveForward().withTimeout(1));
    
    //Put the autoChooser on the SmartDashboard
    SmartDashboard.putData("Auto Chooser", autoChooser);

    // final ShuffleboardTab ShooterTab = Shuffleboard.getTab("timer");
    // this.matchTime = ShooterTab
    //   .add("shift Time", 0)
    //   .getEntry();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary predicate, or via the
   * named factories in {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight joysticks}.
   */

   SwerveInputStream driveDirectAngle = driveAngularVelocity.copy().withControllerHeadingAxis(driverXbox::getRightX,
                                                                                             driverXbox::getRightY)
                                                           .headingWhile(true);




  private void configureBindings()
  {
    Command driveFieldOrientedAnglularVelocity = drivebase.driveFieldOriented(driveAngularVelocity);
    drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocity);

    //OPERATOR COMMANDS
    operatorXbox.rightTrigger().whileTrue(new ShootWithDistance(OUTAKE, LLHandler));
    operatorXbox.rightTrigger().onFalse(new InstantCommand(()->OUTAKE.stop()));

    // reverse funnel;
    operatorXbox.rightBumper().onTrue(new InstantCommand(()->OUTAKE.setFunnelPower(-OutakeConstants.FUNNEL_SPEED)));
    operatorXbox.rightBumper().onFalse(new InstantCommand(()->OUTAKE.setFunnelPower(OutakeConstants.FUNNEL_SPEED)));
    
    // intake
    operatorXbox.leftTrigger().onTrue(new InstantCommand(()->{
      if (BRI_Cancel_Ptr!=null&& BRI_Cancel_Ptr.isScheduled()) BRI_Cancel_Ptr.cancel();
      INTAKE.setSpeed(IntakeConstants.INTAKE_SPEED);
    }));
    operatorXbox.leftTrigger().onFalse(new InstantCommand(()->{
      BRI_Cancel_Ptr = new BriefReverseIntake(INTAKE);
      BRI_Cancel_Ptr.schedule();
    }));

    // Reverse intake
    operatorXbox.leftBumper().onTrue(new InstantCommand(()->INTAKE.outake(IntakeConstants.INTAKE_SPEED)));
    operatorXbox.leftBumper().onFalse(new InstantCommand(()->INTAKE.setSpeed(0)));

    // Shoot constant speed
    operatorXbox.y().onTrue(new InstantCommand(()->OUTAKE.ConstantShoot(OutakeConstants.OUTAKE_SPEED)));
    operatorXbox.y().onFalse(new InstantCommand(()->OUTAKE.stop()));

    // Super outake
    operatorXbox.b().onTrue(new InstantCommand(()->OUTAKE.ConstantShoot(OutakeConstants.Super_OUTAKE_SPEED)));
    operatorXbox.b().onFalse(new InstantCommand(()->OUTAKE.stop()));

    // slow outake
    operatorXbox.a().onTrue(new InstantCommand(()->OUTAKE.ConstantShoot(OutakeConstants.Slow_OUTAKE_SPEED)));
    operatorXbox.a().onFalse(new InstantCommand(()->OUTAKE.stop()));

    // operatorXbox.x().onTrue(new InstantCommand(()->BLENDER.setBlenderRPM(OutakeConstants.BloaderVel)));
    // operatorXbox.x().onFalse(new InstantCommand(()->BLENDER.setBlenderRPM(0)));
    operatorXbox.x().whileTrue(new BlenderPulseCommand(BLENDER, OutakeConstants.PULSE_TIME));

    // adjusts the slowed speed on the robot
    operatorXbox.povLeft().onTrue(new InstantCommand(()->OUTAKE.adjustTrim(-.05)));
    operatorXbox.povRight().onTrue(new InstantCommand(()->OUTAKE.adjustTrim(.05)));
    // operatorXbox.povUp().onTrue(new InstantCommand(()->System.out.println(LLHandler.getDistFromTag(11))));
    //-------------------------------------------------------------------------------------------------------------------
    //DRIVER COMMANDS
    driverXbox.a().onTrue(CenterWheels);
    driverXbox.start().onTrue(new InstantCommand(()-> {
      drivebase.zeroGyroWithAlliance();}));
                                                                                    
     // MOVE TO TAG COMMAND
    driverXbox.b().onTrue(new SequentialCommandGroup(
      new InstantCommand(()-> {drivebase.centerModulesCommand();}),
      new Movetotag(true, drivebase).withTimeout(3)));
                                                                                    
    //aim at tag                                                                                
    driverXbox.y().whileTrue(new AimAtTag(drivebase, LLHandler, driverXbox));
    
    //slow down                                                                       
     driverXbox.rightTrigger()
      .onTrue(new InstantCommand(()->
        drivebase.setMaxSpeedDashBoard())
      ).onFalse(new InstantCommand(()->
        drivebase.setMaxSpeed(1))
    );

    // Shake Command
    driverXbox.leftTrigger().whileTrue(new SwerveShakeRelative(drivebase));
    driverXbox.leftTrigger().onFalse(
      new AimAtTag(drivebase, LLHandler, driverXbox)
          .withTimeout(OperatorConstants.SHAKE_END_TIMEOUT)
    );

    // adjusts the slowed speed on the robot
    driverXbox.povLeft().onTrue(new InstantCommand(()->drivebase.adjustSlowSpeed(-.05)));
    driverXbox.povRight().onTrue(new InstantCommand(()->drivebase.adjustSlowSpeed(.05)));

    driverXbox.x().whileTrue(new InstantCommand(()->drivebase.lock()));
  }

    
  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand()
  {
    // Pass in the selected auto from the SmartDashboard as our desired autnomous commmand 
    return autoChooser.getSelected();
  }

  public void setMotorBrake(boolean brake)
  {
    drivebase.setMotorBrake(brake);
  }

public void periodic() {
    // matchTime.setDouble(DriverStation.getMatchTime());
    // boolean seesTag = LLHandler.seesHubTag();
    // double dist = LLHandler.getBotRadius();

    // if (seesTag && dist >= HapticConstants.HUB_VIBRATE_DISTANCE[0] 
    //            && dist <= HapticConstants.HUB_VIBRATE_DISTANCE[1]) {
    //     operatorXbox.setRumble(RumbleType.kBothRumble, HapticConstants.HUB_DIST_VIBRATE_STRENGTH);
    // } else if (seesTag) {
    //     operatorXbox.setRumble(RumbleType.kBothRumble, HapticConstants.HUB_SEE_VIBRATE_STRENGTH);
    // } else {
    //     operatorXbox.setRumble(RumbleType.kBothRumble, 0.0);
    // }
}

}