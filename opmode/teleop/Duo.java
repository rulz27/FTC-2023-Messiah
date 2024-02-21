package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.ConditionalCommand;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.commands.telecommand.ArmAdjustCommand;
import org.firstinspires.ftc.teamcode.commands.telecommand.ClawToggleCommand;
import org.firstinspires.ftc.teamcode.commands.telecommand.DepositSequence;
import org.firstinspires.ftc.teamcode.commands.telecommand.IntakeSequence;
import org.firstinspires.ftc.teamcode.commands.telecommand.IntermediateSequence;
import org.firstinspires.ftc.teamcode.common.hardware.Global;
import org.firstinspires.ftc.teamcode.common.hardware.WRobot;
import org.firstinspires.ftc.teamcode.common.hardware.drive.Drivetrain;
import org.firstinspires.ftc.teamcode.common.hardware.subsystems.Arm;
import org.firstinspires.ftc.teamcode.common.hardware.subsystems.Drone;
import org.firstinspires.ftc.teamcode.common.hardware.subsystems.Intake;
import org.firstinspires.ftc.teamcode.common.util.Vector2D;
import org.firstinspires.ftc.teamcode.common.util.WMath;

@TeleOp (name = "MainTeleOp - duo")
public class Duo extends CommandOpMode {
    private final WRobot robot = WRobot.getInstance();

    private GamepadEx controller1;
    private GamepadEx controller2;

    private double loop_time = 0.0;

    private double INITIAL_YAW = Global.YAW_OFFSET;  //TODO LINK BETWEEN THE TWO PROGRAMS
    private boolean SLOW_MODE_P1 = false;
    private boolean SLOW_MODE_P2 = false;

    private ElapsedTime timer;

    @Override
    public void initialize() {
        super.reset();

        Global.IS_AUTO = false;
        Global.USING_DASHBOARD = false;
        Global.DEBUG = false;
        Global.USING_IMU = true;
        Global.USING_WEBCAM = false;

        robot.addSubsystem(new Drivetrain(), new Intake(), new Arm(), new Drone());
        robot.init(hardwareMap, telemetry);

        if (Global.USING_DASHBOARD) {
            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
            FtcDashboard.getInstance().startCameraStream(robot.pipeline, 0);
        }

        robot.arm.setArmState(Arm.ArmState.FLAT);
        robot.intake.setWristState(Intake.WristState.FOLD);
        robot.intake.setClawState(Intake.ClawSide.BOTH, Intake.ClawState.CLOSED);

        controller1 = new GamepadEx(gamepad1);
        controller2 = new GamepadEx(gamepad2);

        //toggle claw states
        controller2.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(new ClawToggleCommand(Intake.ClawSide.BOTH));

        controller2.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(new ClawToggleCommand(Intake.ClawSide.LEFT));

        controller2.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(new ClawToggleCommand(Intake.ClawSide.RIGHT));

        //p1 intake controls
        controller1.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(new ConditionalCommand(
                        new IntakeSequence(),
                        new InstantCommand(),
                        () -> Global.STATE == Global.State.INTERMEDIATE
                ));

        controller1.getGamepadButton(GamepadKeys.Button.Y)
                .whenPressed(new ConditionalCommand(
                        new IntermediateSequence(),
                        new InstantCommand(),
                        () -> Global.STATE == Global.State.INTAKE
                ));

        //p2 deposit controls
        controller2.getGamepadButton(GamepadKeys.Button.Y)
                .whenPressed(new ConditionalCommand(
                        new DepositSequence(),
                        new InstantCommand(),
                        () -> Global.STATE == Global.State.INTERMEDIATE
                ));

        controller2.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(new ConditionalCommand(
                        new IntermediateSequence(),
                        new InstantCommand(),
                        () -> Global.STATE == Global.State.SCORING
                ));

        //slow mode
        controller1.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(new InstantCommand(() -> SLOW_MODE_P1 = true))
                .whenReleased(new InstantCommand(() -> SLOW_MODE_P1 = false));

        controller2.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(new InstantCommand(() -> SLOW_MODE_P2 = true))
                .whenReleased(new InstantCommand(() -> SLOW_MODE_P2 = false));

        //drone controls
//        controller2.getGamepadButton(GamepadKeys.Button.Y)
//                .whenPressed(new ConditionalCommand(
//                        new DroneLaunchSequence(),
//                        new InstantCommand(),
//                        this::isEndGame
//                ));
//
//        controller2.getGamepadButton(GamepadKeys.Button.A)
//                .whenPressed(new ConditionalCommand(
//                        new DroneResetSequence(),
//                        new InstantCommand(),
//                        this::isEndGame
//                ));
//
//        controller2.getGamepadButton(GamepadKeys.Button.X)
//                .whenPressed(new DroneResetCommand());
//
//        controller2.getGamepadButton(GamepadKeys.Button.B)
//                .whenPressed(new ConditionalCommand(
//                        new DroneLaunchCommand(),
//                        new InstantCommand(),
//                        () -> Global.STATE == Global.State.LAUNCHING && isEndGame()
//                ));

        //hook controls
//        controller2.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
//                .whenPressed(new ConditionalCommand(
//                        new HangSequence(),
//                        new InstantCommand(),
//                        this::isEndGame
//                ));
//
//        controller2.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
//                .whenPressed(new ConditionalCommand(
//                        new HangRetractSequence(),
//                        new InstantCommand(),
//                        () -> isEndGame() && Global.STATE == Global.State.HANGING
//                ));

        //yaw manual reset methods
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .whenPressed(new InstantCommand(() -> INITIAL_YAW = robot.getYaw()));

        controller1.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
                .whenPressed(new InstantCommand(() -> INITIAL_YAW = robot.getYaw() + Math.PI / 2));

        controller1.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
                .whenPressed(new InstantCommand(() -> INITIAL_YAW = robot.getYaw() - Math.PI / 2));

        controller1.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(new InstantCommand(() -> INITIAL_YAW = robot.getYaw() - Math.PI));

        while (opModeInInit()) {
            telemetry.addLine("Initialization complete.");
            telemetry.update();
        }
    }


    @Override
    public void run() {
        if (timer == null) {
            timer = new ElapsedTime();
            robot.startIMUThread(() -> true);
        }
        robot.read();

        Vector2D local_vector = new Vector2D(controller1.getLeftX(), controller1.getLeftY(),
                WMath.wrapAngle(robot.getYaw() - INITIAL_YAW));
        if (SLOW_MODE_P1) local_vector.scale(0.5);

        //left trigger gets precedent
        if (controller2.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.1) {
            super.schedule(new ArmAdjustCommand(SLOW_MODE_P2 ? -3 : -5));
        }
        else if (controller2.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.1) {
            super.schedule(new ArmAdjustCommand(SLOW_MODE_P2 ? 3 : 5));
        }

        //hang controls
//        if (controller2.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.1 && isEndGame()) {
//            super.schedule(new HangCommand());
//        }
//        else if (controller2.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.1 && isEndGame()) {
//            super.schedule(new HangRetractCommand());
//        }
//        else {
//            super.schedule(new HangStopCommand());
//        }

        super.run();
        robot.periodic();

        robot.drivetrain.move(local_vector, controller1.getRightX() * (SLOW_MODE_P1 ? 0.5 : 1));
        robot.write();
        robot.clearBulkCache(Global.Hub.EXPANSION_HUB);

        double loop = System.nanoTime();
        telemetry.addData("Timer", "%.0f", timer.seconds());
        telemetry.addData("Frequency", "%.2fhz", 1000000000 / (loop - loop_time));
        telemetry.addData("Voltage", "%.2f", robot.getVoltage());
        telemetry.addData("Yaw", "%.2f", WMath.wrapAngle(robot.getYaw() - INITIAL_YAW));
        telemetry.addData("State", Global.STATE);

//        telemetry.addData("dev", robot.hang_actuator.devices.toString());

        if (Global.DEBUG) {
            telemetry.addLine("---------------------------");
            telemetry.addData("arm target", robot.arm.target_position);
            telemetry.addData("arm power", robot.arm.power);
            telemetry.addData("arm state", robot.arm.getArmState());
            telemetry.addData("arm angle", "%.2f", Math.toDegrees(robot.arm.arm_angle.getAsDouble()));


            telemetry.addLine("---------------------------");
            telemetry.addData("wrist target", robot.intake.target_position);
            telemetry.addData("wrist angle", "%.2f", Math.toDegrees(robot.intake.wrist_angle.getAsDouble()));
//
//            telemetry.addLine("---------------------------");
//            telemetry.addData("hang state", robot.hang.hang_state);
//            telemetry.addData("hang power", robot.hang.power);
//            telemetry.addData("hook position", robot.hook.getPosition());
//
//            telemetry.addLine("---------------------------");
//            telemetry.addData("drone state", robot.drone.drone_state);
//            telemetry.addData("drone state", robot.trigger.getPosition());
        }

        telemetry.update();
        loop_time = loop;
    }

    @Override
    public void reset() {
        super.reset();
        robot.reset();
        Global.resetGlobals();
    }

    public boolean isEndGame() {
        return timer.seconds() > 90;
    }
}
