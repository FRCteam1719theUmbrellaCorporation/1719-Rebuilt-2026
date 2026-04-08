// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.DeviceCommands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.BlenderConstants;
import frc.robot.subsystems.devices.BlenderSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class BlenderPulseCommand extends Command {
  Timer pulseTimer;
  BlenderSubsystem blender;
  boolean on;
  double time;

  /** Creates a new BlenderPulseCommand. */
  public BlenderPulseCommand(BlenderSubsystem Blender, double time) {
    this.time = time;
    blender = Blender;
    addRequirements(blender);
  }

  public BlenderPulseCommand(BlenderSubsystem Blender) {
    this(Blender, BlenderConstants.PULSE_TIME_AUTO);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    pulseTimer = new Timer();
    pulseTimer.start();
    on = true;
    //CHANGE LOCATION OF CONSTANT
    blender.setBlenderSpeed(BlenderConstants.BlenderSpeed);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (pulseTimer.hasElapsed(this.time)){
      on = !on;
      //CHANGE LOCATION OF CONSTANT
      blender.setBlenderSpeed(on ? BlenderConstants.BlenderSpeed:0);
      pulseTimer.reset();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    blender.setBlenderSpeed(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
