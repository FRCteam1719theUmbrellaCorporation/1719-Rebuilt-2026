// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.DeviceCommands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.OutakeConstants;
import frc.robot.subsystems.devices.BlenderSubsystem;


/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class BlenderBackPulseCommand extends Command {
  Timer pulseTimer;
  BlenderSubsystem blender;
  boolean on;
  double blenPow = OutakeConstants.BloaderVel;

  /** Creates a new BlenderPulseCommand. */
  public BlenderBackPulseCommand(BlenderSubsystem Blender) {
    // Use addRequirements() here to declare subsystem dependencies.
    blender = Blender;
    addRequirements(blender);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    pulseTimer = new Timer();
    pulseTimer.start();
    on = true;
    blender.setBlenderRPM(OutakeConstants.BloaderVel);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (pulseTimer.hasElapsed(OutakeConstants.PULSE_BACK_TIME)){
      on = !on;
      blenPow = -blenPow;
      blender.setBlenderRPM(blenPow);
      pulseTimer.reset();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    blender.setBlenderRPM(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
