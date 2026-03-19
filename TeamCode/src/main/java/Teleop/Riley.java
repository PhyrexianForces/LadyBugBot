import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;

import java.lang.Math;

@Teleop
public class rileyiscool extends OpMode{
  private DcMotorEx frontLeft, frontRight, backLeft, backRight;
  private DcMotorEx flywheelRight, flywheelLeft;
  private CRServo leaderServo;

  @Override
  public void init() {
    telemetry.addData("Heyo World")
  }

  @Override
  public void init() {
    frontLeft = hardwareMap.get(DcMotorEx.class, "fl")
    frontRight = hardwareMap.get(DcMotorEx.class, "fr")
    backLeft = hardwareMap.get(DcMotorEx.class, "bl")
    backRight = hardwareMap.get(DcMotorEx.class, "br")
    wheelLeft = hardwareMap.get(DcMotorEx.class, "wl")
    wheelRight = hardwareMap.get(DcMotorEx.class, "wr")

    //Determines 
    frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
    backLeft.setDirection(DcMotorEx.Direction.REVERSE);
    frontRight.setDirection(DcMotorEx.Direction.FORWARD);
    frontRight.setDirection(DcMotorEx.Direction.FORWARD);
    wheelLeft.setDirection.(DcMotor.Direction.FORWARD);
    wheelRight.setDirection.(DcMotor.Direction.REVERSE);
  }

  public void driveOmni(double x, double y, double rx) {
    final double MTPS = 4661;
    //Finds the greatest, positive, not float value among x, y, and rx
    double maxValue = Math.max(Math.abs(x) + Math.abs(y) + Math.abs(rx), 1);

    //Math stuff
    double flPower = (y + x + rx) / maxValue;
    double blPower = (y - x + rx) / maxValue;
    double frPower = (y - x - rx) / maxValue;
    double brPower = (y + x - rx) / maxValue;

    frontLeft.setVelocity(flPower * MTPS);
    frontRight.setVelocity(flPower * MTPS);
    backLeft.setVelocity(flPower * MTPS);
    backRight.setVelocity(flPower * MTPS);
  }



  @Override
  public void loop() 
  {
   //Determines the inputs of the gamepad
   double y = gamepad1.left_stick_y;
   double x = gamepad1.left_stick_x;
   double rx = gamepad1.right_stick_x;
   driveOmni(y, x, rx);
   if (gamepad1.x) {
     wheelRight.setPower(1);
     wheelLeft.setPower(1);
     try {
       //Pauses program
       Thread.sleep(1500)
     } catch (InterruptedException e) {
       throw new RuntimeException(e);
     }
   } else if (gamepad1.x) {
     telemetry.addData("Bonjour!")
     loaderServo.setPower(1);

     try {
       //Pauses program
       Thread.sleep(500)
     } catch (InterruptedException e) {
       throw new RuntimeException(e);
   } else {
     //Contingency in case of not pressing button to power down flywheels
     wheelRight.setPower(0);
     wheelLeft.setPower(0);
     loaderServo.setPower(0);
     }  
  }
}
