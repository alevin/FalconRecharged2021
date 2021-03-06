// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.AutoPaths;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.TrajectoryHelper;
import frc.robot.commands.conveyor.SenseNewPowerCell;
import frc.robot.commands.intake.IntakeSpeed;
import frc.robot.commands.swervedrive.Autonomous;
import frc.robot.subsystems.ConveyorTalon;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Drive.SwerveDriveSubsystem;
import frc.robot.utility.TrajectoryMaker;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class GalacticSearch extends SequentialCommandGroup {
  /** Creates a new SensorTest. */
  public static final double intakeSpeed = -.5;
  public boolean galacticSearchDone = false;

  public void setDone()
  {
    galacticSearchDone = true;
  }

  public boolean getDone()
  {
    return galacticSearchDone;
  }

  public Command conditional(SwerveDriveSubsystem swerveDriveSubsystem, Intake intake, ConveyorTalon conveyor, TrajectoryMaker hasSeenTrajectory, TrajectoryMaker notSeenTrajectory)
  {
    return new ConditionalCommand(
      new Finish_Auton(swerveDriveSubsystem, hasSeenTrajectory, this)
        .raceWith(new IntakeSpeed(intake, intakeSpeed))
          .raceWith(new SenseNewPowerCell(conveyor)).andThen(()->setDone()), 
      new Finish_Auton(swerveDriveSubsystem, notSeenTrajectory, this)
        .raceWith(new IntakeSpeed(intake, intakeSpeed))
          .raceWith(new SenseNewPowerCell(conveyor)),
      conveyor::getHasSeen
    );
  }

  public GalacticSearch(SwerveDriveSubsystem swerveDriveSubsystem, Intake intake, ConveyorTalon conveyor) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    // Start_to_B3 --> if (found B3) B3_to_Finish 
    // else B3_to_C3 --> if (found C3) C3_to_Finish
    // else C3_to_D6 --> if (found D6) D6_to_Finish_A
    // else D6_to_Finish_B
    
    super();
    addCommands(
    new InstantCommand(intake::lowerIntake, intake),
    new Autonomous(swerveDriveSubsystem, TrajectoryHelper.Start_to_B3().getTrajectory(), TrajectoryHelper.Start_to_B3().getAngle()).withTimeout(3)
    .raceWith(new IntakeSpeed(intake, intakeSpeed)).raceWith(new SenseNewPowerCell(conveyor)),
    conditional(swerveDriveSubsystem, intake, conveyor, TrajectoryHelper.B3_to_Finish(), TrajectoryHelper.B3_to_C3()),
    conditional(swerveDriveSubsystem, intake, conveyor, TrajectoryHelper.C3_to_Finish(), TrajectoryHelper.C3_to_D6()),
    conditional(swerveDriveSubsystem, intake, conveyor, TrajectoryHelper.D6_to_Finish_A(), TrajectoryHelper.D6_to_Finish_B()),
    //new InstantCommand(swerveDriveSubsystem::stopDriveMotors, swerveDriveSubsystem),
    new IntakeSpeed(intake, 0)
    );
  }
    public static double[][] Start_to_B3= {
        {30,120},
        {90,120},
    };

    public static double[][] B3_to_C3=  {
        {90,120},
        {90,90},
    };

    public static double[][] B3_to_Finish = {
        {90,120},
        {150,60},
        {180,150},
        {345,150},
    };

    public static double[][] C3_to_D6 = {
        {90, 90},
        {180, 60},
      };

    public static double[][] C3_to_Finish = {
        {90,120},
        {90,90},
        {150,60},
        {180,150},
        {345,150},
    };

    public static double[][] D6_to_Finish_A = {
        {180,60},
        {240,120},
        {300,60},
        {345,60},
    };

    public static double[][] D6_to_Finish_B = {
        {180,60},
        {180,30},
        {210,120},
        {270,90},
        {345,90},
    };
}

