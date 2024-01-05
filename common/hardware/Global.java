package org.firstinspires.ftc.teamcode.common.hardware;

public class Global {
    public enum Hub {CONTROL_HUB, EXPANSION_HUB, BOTH}
    public enum Side {BLUE, RED}
    public enum PropLocation {LEFT, RIGHT, CENTER}

    public static final int MOTOR_TPR = 1440;
    public static double INITIAL_YAW;

    public static boolean USING_DASHBOARD;
    public static boolean IS_AUTO;
    public static boolean USING_IMU;
    public static boolean USING_WEBCAM;
    public static boolean DEBUG;
    public static Side SIDE;

    public static boolean IS_SCORING = false;
    public static boolean IS_INTAKING = false;

    public static void resetGlobals() {
        IS_SCORING = false; IS_INTAKING = false;
        USING_DASHBOARD = false; IS_AUTO = false; USING_IMU = false; USING_WEBCAM = false; DEBUG = false; SIDE = null;
    }

    public static void startScoring() {
        IS_SCORING = true;
    }

    public static void stopScoring() {
        IS_SCORING = false;
    }

    public static void startIntaking() {
        IS_INTAKING = true;
    }

    public static void stopIntaking() {
        IS_INTAKING = false;
    }
}
