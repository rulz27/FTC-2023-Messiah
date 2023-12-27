package org.firstinspires.ftc.teamcode.hardware.wrappers;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.RobotHardware;
import org.firstinspires.ftc.teamcode.controllers.PIDF;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.WMath;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class WActuator {
    public final HashMap<String, HardwareDevice> devices = new HashMap<>();

    private DoubleSupplier voltage;
    public ElapsedTime timer;
    private Robot robot = Robot.getInstance();

    private double target_position = 0.0;
    private double current_position = 0.0;
    private double offset = 0.0;
    private double power = 0.0;

    private Supplier<Object> topic;

    public WActuator(HardwareDevice... devices) {
        this.topic = null;
        int id = 0;
        for (HardwareDevice device : devices) {
            this.devices.put(device.getDeviceName() + " " + id, device);
        }
        read();
    }

    public WActuator(Supplier<Object> topic, HardwareDevice... devices) {
        this.topic = topic;
        int id = 0;
        for (HardwareDevice device : devices) {
            this.devices.put(device.getDeviceName() + " " + id, device);
        }
        read();
    }

    public void periodic() {
    }

    public void read() {
        if (topic != null) {
            Object value = topic.get();
            if (value instanceof Integer) {
                this.current_position = (int) value + offset;       //CORRECT???
                return;
            } else if (value instanceof Double) {
                this.current_position = (double) value + offset;
                return;
            }
        }

        for (HardwareDevice device : devices.values()) {
            if (device instanceof WAnalogEncoder) {
                this.current_position = ((WAnalogEncoder) device).getPosition() + offset;
                return;
            } else if (device instanceof WEncoder) {
                this.current_position = ((WEncoder) device).getPosition() + offset;
                return;
            }
        }
        this.current_position = 0.0;
    }

    public void write() {
        int i = 0;
        for (HardwareDevice device : devices.values()) {
            if (device instanceof DcMotor) {
//                double correction = 1.0;
//                if (voltage != null) correction = 12.0 / voltage.getAsDouble();
//                if (!floating) ((DcMotor) device).setPower(power * correction);
//                else ((DcMotor) device).setPower(0);
                ((DcMotor) device).setPower(WMath.clamp(power, -1, 1));
            } else if (device instanceof Servo) {
                ((Servo) device).setPosition(target_position);
            }
        }
    }

    public void reset() {}

    public void setPower(double power) {
        this.power = power;
    }

    public void setTargetPosition(double target_position) {
        this.target_position = target_position;
    }

    /**
     *
     * @param offset offset current_position by + offset
     * @return
     */
    public WActuator setOffset(double offset) {
        this.offset = offset;
        return this;
    }

    public double getOffset() {
        return offset;
    }

    public double getCurrentPosition() {
        return current_position;
    }

    public WActuator setVoltageSupplier(DoubleSupplier voltage) {
        this.voltage = voltage;
        return this;
    }

}