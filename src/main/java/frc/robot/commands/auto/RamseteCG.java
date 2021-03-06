// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.SneakyTrajectory;
import frc.robot.subsystems.DriveSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class RamseteCG extends SequentialCommandGroup {
    /** Creates a new RamseteCG. */
    public RamseteCG(SneakyTrajectory s_trajectory, DriveSubsystem drive) {
        // Add your commands in the addCommands() call, e.g.
        // addCommands(new FooCommand(), new BarCommand());
        super(
                s_trajectory
                        .getRamsete(s_trajectory.testAuto[0])
                        .andThen(() -> drive.tankDriveVolts(0, 0)));
    }
}
