/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.AutoPaths;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.TrajectoryHelper;
import frc.robot.utility.TrajectoryMaker;
import frc.robot.commands.swervedrive.Autonomous;
import frc.robot.subsystems.Drive.SwerveDriveSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class Finish_Auton extends SequentialCommandGroup {
  /**
   * Creates a trajectory that runs 
   * if galactic search is done ends early
   */
  public GalacticSearch search;

  @Override
  public boolean isFinished() {
    return super.isFinished() || search.getDone();
  }

  public Finish_Auton(SwerveDriveSubsystem swerveDriveSubsystem, TrajectoryMaker endTrajectory, GalacticSearch galacticSearch) {  // test forward path
   
    super(
      new Autonomous(swerveDriveSubsystem, endTrajectory.getTrajectory(), endTrajectory.getAngle()).withTimeout(5)
    );
    this.search = galacticSearch; 
  }

  
}