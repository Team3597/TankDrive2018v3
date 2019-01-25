package org.usfirst.frc.team3597.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {
	
	private static float speed;
	public static Spark leftMotor;
	public static Spark rightMotor;
	private static DifferentialDrive Robot;
	private static boolean buttonValue = false;
	
	//Creates a new DriveTrain object
	public DriveTrain (int leftPort, int rightPort, float speed) {
		DriveTrain.speed = speed;
		leftMotor = new Spark(leftPort);
		rightMotor = new Spark(rightPort);
		DriveTrain.Robot = new DifferentialDrive(leftMotor, rightMotor);
	}
	
	//Sets the left and right motors to their set speed
	public static void drive(double leftMotorSpeed, double rightMotorSpeed) {
		if (leftMotorSpeed != 0) {
			Robot.tankDrive(leftMotorSpeed + 0.03d, rightMotorSpeed);
		} else {
			Robot.tankDrive(leftMotorSpeed, rightMotorSpeed);
		}
	}
	
	//Calculates the speed the left motor should be
	public static double getLeftMotorSpeed (Joystick controller, int leftJoystickYAxis) {
		return (double) (controller.getRawAxis(leftJoystickYAxis) * speed);
	}
	
	//Calculates the speed the right motor should be
	public static double getRightMotorSpeed (Joystick controller, int rightJoystickYAxis) {
		return (double) (controller.getRawAxis(rightJoystickYAxis) * speed);
	}
	
	public static void changeDriveSpeed (Joystick controller, int shooterButton, float defaultSpeed) {
		
		//Display drive speed
		SmartDashboard.putBoolean ("Slow Speed", buttonValue);
		
		//Get Button Input to Control Speed
		boolean shooterButtonValue = controller.getRawButton(shooterButton);
		
		
		//Increase speed on button A & decrease on button B
		if (shooterButtonValue && buttonValue) {
			speed = defaultSpeed;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			buttonValue = false;
		} else if (shooterButtonValue) {
			speed = defaultSpeed / 1.25f;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			buttonValue = true;
		}
		
	}
}
