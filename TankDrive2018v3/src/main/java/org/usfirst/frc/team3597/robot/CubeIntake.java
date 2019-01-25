package org.usfirst.frc.team3597.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;

public class CubeIntake {
	
	private static Spark leftIntakeMotor;
	private static Spark rightIntakeMotor;
	private static Spark armMotor;
	public static CubeShooter shooter;
	
	private static float speed = 1f;
	
	//Creates a CubeIntake object
	public CubeIntake (int leftIntakeMotor, int rightIntakeMotor, int armMotor,
			int leftShooterMotor, int rightShooterMotor) {
		CubeIntake.leftIntakeMotor = new Spark(leftIntakeMotor);
		CubeIntake.rightIntakeMotor = new Spark(rightIntakeMotor);
		CubeIntake.armMotor = new Spark(armMotor);
		shooter = new CubeShooter(leftShooterMotor, rightShooterMotor, speed);
	}
	
	//Sets the shooter motors to moving or not moving
	public static void intake(boolean intaking, boolean feedingX, boolean feedingY, boolean feedingB) {
		if (intaking) {
			System.out.println("Intake");
			leftIntakeMotor.set(-0.8f);
			rightIntakeMotor.set(-0.8f);
		} else  if (feedingX || feedingY || feedingB) {
			//Comment out:
			//if you want to use X & B to change speed of the shooter, not shoot
			if (feedingX) shooter.speed = 0.6f; //Med
			if (feedingY) shooter.speed = 1f; //High //Usually high, if assigned to high again be sure to assign another the medium (Button X)
			if (feedingB) shooter.speed = 0.35f; //Low //Please keep this the same value or autonomous will break
			//Comment ends here
			System.out.println("Feed");
			leftIntakeMotor.set(1);
			rightIntakeMotor.set(1);
			shooter.shoot(true);
		} else {
			leftIntakeMotor.set(0);
			rightIntakeMotor.set(0);
			shooter.shoot(false);
		}
	}
	
	
	public static void moveArm (boolean moveUp, boolean moveDown) {
		if (moveDown) {
			System.out.println("Move Down");
			armMotor.set(0.3f);
		} else if (moveUp) {
			System.out.println("Move Up");
			armMotor.set(-0.5f);
		} else {
			armMotor.set(0);
		}
	}
	
	public static boolean getButtonValue (Joystick controller, int button) {
		return controller.getRawButton(button);
	}
	
	public static void changeShooterSpeed (Joystick controller, int shooterButtonUp, int shooterButtonDown, float defaultSpeed) {

		//Get Button Input to Control Speed
		boolean shooterButtonUpValue = controller.getRawButton(shooterButtonUp);
		boolean shooterButtonDownValue = controller.getRawButton(shooterButtonDown);
		
		if (shooterButtonUpValue) {
			speed = defaultSpeed; //High
		} else if (shooterButtonDownValue) {
			//EDIT:
			//Edit the value the default speed is divided by in order to change
			//how much you'd like the shooter speed to decrease
			speed = defaultSpeed / 2.5f; //Low
		}
		
		shooter.speed = speed;
	}
}
