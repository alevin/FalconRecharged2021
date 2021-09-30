/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.commands.AutoPaths.AutoPath1;
import frc.robot.commands.swervedrive.*;

import frc.robot.subsystems.Drive.SwerveDriveModule;
import frc.robot.subsystems.Drive.SwerveDriveSubsystem;

import frc.robot.utility.TrajectoryMaker;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class SkeletonContainer {
  // The robot's subsystems and commands are defined here...

  private final XboxController mXboxController;
  // private final XboxController mXboxController2; //operator controller

  private final SwerveDriveSubsystem swerveDriveSubsystem;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public SkeletonContainer() {
    // create all the subsystems needed in this robot
    SwerveDriveModule m0 = new SwerveDriveModule(0, new TalonSRX(SkeletonConstants.ANGLE2_TALON),
        new TalonFX(SkeletonConstants.DRIVE2_TALON), 128); // real:390 practice: 212
    SwerveDriveModule m1 = new SwerveDriveModule(1, new TalonSRX(SkeletonConstants.ANGLE1_TALON),
        new TalonFX(SkeletonConstants.DRIVE1_TALON), 340); // real:293 practice: 59
    SwerveDriveModule m2 = new SwerveDriveModule(2, new TalonSRX(SkeletonConstants.ANGLE3_TALON),
        new TalonFX(SkeletonConstants.DRIVE3_TALON), 96); // real:298 practice: 56
    SwerveDriveModule m3 = new SwerveDriveModule(3, new TalonSRX(SkeletonConstants.ANGLE4_TALON),
        new TalonFX(SkeletonConstants.DRIVE4_TALON), 164); // real: 355 practice: 190 //
                                                           // ````````````````````````````````````````````````````````````````````````````````````````````````````````357

    swerveDriveSubsystem = new SwerveDriveSubsystem(m0, m1, m2, m3);
    swerveDriveSubsystem.zeroGyro();

    // create the input controllers
    mXboxController = new XboxController(0);
    // mXboxController2 = new XboxController(1);

    // setup any default commands
    swerveDriveSubsystem.setDefaultCommand(new HolonomicDriveCommand(swerveDriveSubsystem, mXboxController));

    // configure the buttons
    configureButtonBindingsForAuto();
    // configureButtonsForPowerPort();
  }

  public void configureButtonsForPowerPort() {
    JoystickButton buttonA = new JoystickButton(mXboxController, XboxController.Button.kA.value);
    JoystickButton buttonX = new JoystickButton(mXboxController, XboxController.Button.kX.value);
    JoystickButton buttonB = new JoystickButton(mXboxController, XboxController.Button.kB.value);
    JoystickButton buttonY = new JoystickButton(mXboxController, XboxController.Button.kY.value);
    JoystickButton leftBumper = new JoystickButton(mXboxController, XboxController.Button.kBumperLeft.value);
    JoystickButton rightBumper = new JoystickButton(mXboxController, XboxController.Button.kBumperRight.value);
    JoystickButton back = new JoystickButton(mXboxController, XboxController.Button.kBack.value);
    JoystickButton start = new JoystickButton(mXboxController, XboxController.Button.kStart.value);
    JoystickButton stickLeft = new JoystickButton(mXboxController, XboxController.Button.kStickLeft.value);
    JoystickButton stickRight = new JoystickButton(mXboxController, XboxController.Button.kStickRight.value);

    back.whileHeld(new ZeroNavX(swerveDriveSubsystem));

  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindingsForAuto() {
    JoystickButton buttonA = new JoystickButton(mXboxController, XboxController.Button.kA.value);
    JoystickButton buttonX = new JoystickButton(mXboxController, XboxController.Button.kX.value);
    JoystickButton buttonB = new JoystickButton(mXboxController, XboxController.Button.kB.value);
    JoystickButton buttonY = new JoystickButton(mXboxController, XboxController.Button.kY.value);
    JoystickButton leftBumper = new JoystickButton(mXboxController, XboxController.Button.kBumperLeft.value);
    JoystickButton rightBumper = new JoystickButton(mXboxController, XboxController.Button.kBumperRight.value);
    JoystickButton back = new JoystickButton(mXboxController, XboxController.Button.kBack.value);
    JoystickButton start = new JoystickButton(mXboxController, XboxController.Button.kStart.value);
    JoystickButton stickLeft = new JoystickButton(mXboxController, XboxController.Button.kStickLeft.value);
    JoystickButton stickRight = new JoystickButton(mXboxController, XboxController.Button.kStickRight.value);

    /*
     * The following is an example of an inline command. No need to create a
     * CommandBase Subclass for simple commands
     */

    buttonX.whenPressed(new ToggleFieldOrientedCommand(swerveDriveSubsystem));

    Command unoCycle = new AutoPath1(swerveDriveSubsystem);
    buttonB.whenPressed(unoCycle);

    // buttonB.whenPressed(new Autonomous(swerveDriveSubsystem,
    // path2Meters.getTrajectory(), path2Meters.getAngle(), true));

    back.whileHeld(new ZeroNavX(swerveDriveSubsystem));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    // How can we change this to select the auto routine from the dashboard?
    // return new AutoPath1(swerveDriveSubsystem);

    // TrajectoryMaker path0 = TrajectoryHelper.createSlalom();
    // TrajectoryMaker path = TrajectoryHelper.createBarrel();

    // Command autoCommand0 = new Autonomous(swerveDriveSubsystem,
    // path0.getTrajectory(), path0.getAngle(), true);
    // Command autoCommand1 = new Autonomous(swerveDriveSubsystem,
    // path.getTrajectory(), path.getAngle(), false);

    TrajectoryMaker path00 = TrajectoryHelper.createBounce00();
    TrajectoryMaker path01 = TrajectoryHelper.createBounce01();
    TrajectoryMaker path10 = TrajectoryHelper.createBounce10();

    Command autoCommand00 = new Autonomous(swerveDriveSubsystem, path00.getTrajectory(), path00.getAngle(), true);
    Command autoCommand01 = new Autonomous(swerveDriveSubsystem, path01.getTrajectory(), path01.getAngle(), false);
    Command autoCommand10 = new Autonomous(swerveDriveSubsystem, path10.getTrajectory(), path10.getAngle(), false);

    return new AutoPath1(swerveDriveSubsystem);
  }

}