package org.usfirst.frc.team3597.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Victor;

public class CubeShooter {
	
	private static Victor leftShooterMotor;
	private static Victor rightShooterMotor;
	public static double speed;
	
	//Creates a CubeShooter object
	public CubeShooter (int leftShooterMotor, int rightShooterMotor, double speed) {
		CubeShooter.leftShooterMotor = new Victor(leftShooterMotor);
		CubeShooter.rightShooterMotor = new Victor(rightShooterMotor);
		CubeShooter.speed = speed;
	}
	
	//Sets the shooter motors to moving or not moving
	public void shoot(boolean shooting) {
		if (shooting) {
			leftShooterMotor.set(speed);
			rightShooterMotor.set(speed);
		} else {
			leftShooterMotor.set(0);
			rightShooterMotor.set(0);
		}
	}
}
