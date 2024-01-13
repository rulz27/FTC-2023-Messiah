package org.firstinspires.ftc.teamcode.commands.subsystemcommand;

import com.arcrobotics.ftclib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.common.hardware.WRobot;

public class ArmResetPosition extends InstantCommand {
    public ArmResetPosition(WRobot robot) {
        super (
                () -> new ArmSetTargetCommand(robot, (double) robot.arm_actuator.getReadingOffset())
        );
    }
}
