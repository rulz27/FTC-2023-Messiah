package org.firstinspires.ftc.teamcode.opmode.tests.vision;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.common.hardware.Global;
import org.firstinspires.ftc.teamcode.common.vision.PropPipeline;
import org.firstinspires.ftc.vision.VisionPortal;
import org.opencv.core.Scalar;


@TeleOp(name = "PipelineTest", group = "Utility")
public class PipelineTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        PropPipeline prop_pipeline;
        VisionPortal vision_portal;

        Global.SIDE = Global.Side.RED;
        Global.USING_DASHBOARD = true;
        Global.DEBUG = true;


        prop_pipeline = new PropPipeline();
        vision_portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam"))
                .setCameraResolution(new Size(640, 480))
                .addProcessor(prop_pipeline)
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .enableLiveView(true)
                .setAutoStopLiveView(true)
                .build();

        if (Global.USING_DASHBOARD) {
            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
            FtcDashboard.getInstance().startCameraStream(prop_pipeline, 0);
        }

        while (opModeInInit()) {
            telemetry.addData("Side", Global.SIDE);
            if (PropPipeline.filter_range != null) {
                telemetry.addData("filter", PropPipeline.filter_range[0] + "," +
                        PropPipeline.filter_range[1] + "," +
                        PropPipeline.filter_range[2] + "," +
                        PropPipeline.filter_range[3] + "," +
                        PropPipeline.filter_range[4] + "," +
                        PropPipeline.filter_range[5]);
            }
            telemetry.addData("Location", prop_pipeline.getPropLocation());
            telemetry.addData("# of detections", prop_pipeline.getNumberOfDetection());
            telemetry.addData("leftZone", prop_pipeline.left_white);
            telemetry.addData("centerZone", prop_pipeline.center_white);
            telemetry.addData("threshold", prop_pipeline.threshold);
            telemetry.addData("FPS", vision_portal.getFps());
            telemetry.update();
        }

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Location", prop_pipeline.getPropLocation());
            telemetry.update();
        }
    }
}