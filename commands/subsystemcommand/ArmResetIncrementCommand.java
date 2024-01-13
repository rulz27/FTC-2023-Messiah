package org.firstinspires.ftc.teamcode.commands.subsystemcommand;

import com.arcrobotics.ftclib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.common.hardware.WRobot;
import org.firstinspires.ftc.teamcode.common.hardware.subsystems.Arm;

public class ArmResetIncrementCommand extends InstantCommand {
    public ArmResetIncrementCommand(WRobot robot) {
        super (
                robot.arm::resetIncrement
        );
    }
}
