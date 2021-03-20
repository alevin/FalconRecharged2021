// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project. 

package frc.robot.commands.AutoPaths;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.WaitForConveyor;
import frc.robot.commands.conveyor.SenseCell;
import frc.robot.commands.conveyor.SenseNewPowerCell;
import frc.robot.commands.intake.IntakeSpeed;
import frc.robot.subsystems.ConveyorTalon;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Drive.SwerveDriveSubsystem;

public class GalacticSearch extends SequentialCommandGroup {
  /** Creates a new SensorTest. */
  public static final double intakeSpeed = -.5;
  public static final double INTAKE_DELAY = 1.0;
  // delay for a second when we get to a choice point, to ensure conveyor can notice the ball
  private boolean galacticSearchDone = false;
  ConveyorTalon conveyorTalon;
  Intake intake;

  @Override
  public void end(boolean interrupted) {
    super.end(interrupted);
    conveyorTalon.setConveyorSpeed(0);
    intake.setSpeed(0);
  }

  public void setDone()
  {
    galacticSearchDone = true;
  }

  public boolean getDone()
  {
    return galacticSearchDone;
  }
  /**
   * creates a conditional command that runs the hasSeenTrajectory if a powercell is in the conveyor,
   * otherwise runs notSeenTrajectory
   * if run hasSeenTrajectory setDone following commands don't run
   * @param swerveDriveSubsystem
   * @param intake
   * @param conveyor
   * @param hasSeenTrajectory
   * @param notSeenTrajectory
   * @return
   */
  public Command conditional(SwerveDriveSubsystem swerveDriveSubsystem, Intake intake, ConveyorTalon conveyor, double[][] hasSeenTrajectory, double[][] notSeenTrajectory)
  {
    return new ConditionalCommand(
      new Finish_Auton(swerveDriveSubsystem, hasSeenTrajectory, this)
          .raceWith(new SenseCell(conveyor)).andThen(()->setDone()),
      new Finish_Auton(swerveDriveSubsystem, notSeenTrajectory, this)
          .raceWith(new SenseCell(conveyor)),
      conveyor::getHasSeen
    );
  }

  @Override
  public void initialize() {
    super.initialize();
    galacticSearchDone = false;
  }

  public GalacticSearch(SwerveDriveSubsystem swerveDriveSubsystem, Intake intake, ConveyorTalon conveyor) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    // Waiting 1 second to check for the power cell
    // Start_to_B3 --> if (found B3) B3_to_Finish 
    // else B3_to_C3 --> if (found C3) C3_to_Finish
    // else C3_to_D6 --> if (found D6) D6_to_Finish_A
    // else D6_to_Finish_B

    super();
    conveyorTalon = conveyor;
    this.intake = intake;
    addCommands(
    new InstantCommand(intake::lowerIntake, intake),
    new InstantCommand(() -> intake.setSpeed(intakeSpeed),intake),
    new Finish_Auton(swerveDriveSubsystem, Start_to_B3, this).raceWith(new SenseCell(conveyor)), 
    new WaitForConveyor(conveyor),
    conditional(swerveDriveSubsystem, intake, conveyor, B3_to_Finish, B3_to_C3),
    new WaitForConveyor(conveyor),
    conditional(swerveDriveSubsystem, intake, conveyor, C3_to_Finish, C3_to_D6),
    new WaitForConveyor(conveyor),
    conditional(swerveDriveSubsystem, intake, conveyor, D6_to_Finish_A, D6_to_Finish_B),
    new InstantCommand(swerveDriveSubsystem::stopDriveMotors, swerveDriveSubsystem),
    new InstantCommand(() -> intake.setSpeed(0),intake)
    );
  }

  /*
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
    new Finish_Auton(swerveDriveSubsystem, driveForward, this).raceWith(new IntakeSpeed(intake, intakeSpeed)).raceWith(new SenseNewPowerCell(conveyor)),
    conditional(swerveDriveSubsystem, intake, conveyor, driveRight, driveLeft),
    new InstantCommand(swerveDriveSubsystem::stopDriveMotors, swerveDriveSubsystem),
    new IntakeSpeed(intake, 0)
    );
  } */

    private static final double[][] driveForward = {
      {30,120},
      {90,120},
    };

    private static final double[][] driveRight = {
      {90,120},
      {90,150},
    };

    private static final double[][] driveLeft = {
      {90,120},
      {90,90},
    };

    // measuring from top down (0 is the top, 180 bottom)
    // (0,0) is the top left point
    // (0,180) is bottom left point
    // (360, 0) is top right point
    // (360, 180) is bottom right point
    // (15, y) center of the start zone
    // (345, y) center of the end zone, x set to 300 for yard
    private static final double[] A6 = {180, 30};
    private static final double[] B1 = {15, 60};
    private static final double[] B3 = {90, 60};
    private static final double[] B3_Front = {130, 60}; //Robot goes forward more to avoid bumping C3 powercell
    private static final double[] B7 = {210, 60};
    private static final double[] B8 = {240, 60};
    private static final double[] C3 = {90, 90};
    private static final double[] C9 = {270, 90};
    private static final double[] D5 = {150, 120};
    private static final double[] D6 = {180, 120}; //robot misses this point
    private static final double[] D6_Front = {220, 120}; //avoid bumping E6 powercell
    private static final double[] D10 = {300, 120};
    private static final double[] E6 = {180, 150};
    private static final double[] B3_END = {300, 60};
    private static final double[] C3_END = {300, 30};
    private static final double[] D6_END_A = {300, 120};
    private static final double[] D6_END_B = {300, 90};

    private static final double[][] Start_to_B3= {
        B1,
        B3,
    };

    private static final double[][] B3_to_C3=  {
        B3,
        B3_Front,
        C3,
    };

    //345 finish?
    private static final double[][] B3_to_Finish = {
      B3,
      D5,
      B7,
      B3_END,
    };

    private static final double[][] C3_to_D6 = {
      C3,
      D6,
    };

    private static final double[][] C3_to_Finish = {
      C3,
      D5,
      A6,
      C3_END,
    };

    private static final double[][] D6_to_Finish_A = {
      D6,
      B8,
      D10,
      D6_END_A,
    };

    private static final double[][] D6_to_Finish_B = {
      D6,
      D6_Front,
      E6,
      B7,
      C9,
      D6_END_B,
    };
}

