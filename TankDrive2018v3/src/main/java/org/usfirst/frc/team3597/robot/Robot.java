package org.usfirst.frc.team3597.robot;

//READ ME!

//I've made some changes to the code. Now the X & B buttons can either shoot or be used to change speeds.
//Currently the program is setup to shoot using the X, Y, and B buttons however this can be changed by commenting out and in
//some code located at the bottom of this page as well as some code in the CubeIntake file. DO NOT HAVE
//BOTH NOT COMMENTED OUT. If you'd like to use the buttons to shoot comment out the code at the bottom of
//this page. If you'd like to use the buttons to change speeds comment out the code in CubeIntake.

//Using the buttons to shoot (Enabled)

//The X, Y, and B buttons will all act like shoot buttons.
//X - Highest speed
//Y - Medium speed
//B - Lowest speed
//If you'd like to change the order of the speeds or fine tune them you can change those values in CubeIntake
//in the Intake method.

//Using the buttons to change speed (Disabled)

//The X and B buttons will change between two speeds
//X - Highest speed
//B - Lowest speed
//Y - Shoots

//With this you can change the speed and then use the Y button to shoot. If you'd like to change the order of the
//speeds or find tune them you can chage those values in CubeIntake under changeShooterSpeed.

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;

public class Robot extends IterativeRobot {
	
	//Autonomous
	private int robotPosition = 1;
	private String gameData;
	// Keeps track of time state was entered
    private Timer autonStateTimer;
    // Keeps track of current state
    private int autonState;
    // List of possible states    
    private final static int AUTON_STATE_DRIVE_FORWARD = 1;
    private final static int AUTON_STATE_STOP = 2;
    private final static int AUTON_STATE_SHOOT = 3;
    private final static int AUTON_STATE_FINISHED = 4;
    private final static int AUTON_STATE_DRIVE_TURN = 5;
    private final static int AUTON_STATE_DRIVE_TURNBACK = 6;
    private final static int AUTON_STATE_DRIVE_FORWARD_BYAHAIR = 7;
	
	//Controller
	private static Joystick driveController;
	private static Joystick shooterController;
	
	//DriveTrain
	DriveTrain RobotDrive;
	float defaultSpeed;
	
	//CubeIntake
	CubeIntake RobotIntake;
	
	public void robotInit() {
		System.out.println("Robot Initializing!");
		
		//Controller setup
		driveController = new Joystick(IO.DRIVE_CONTROLLER);
		shooterController = new Joystick(IO.SHOOTER_CONTROLLER);
		
		//Robot setup
		defaultSpeed = 0.8f; //DEFAULT DRIVE SPEED. PRETTY IMPORTANT THING PEOPLE LIKE TO CHANGE ALL TOO MUCH! :)
		RobotDrive = new DriveTrain(IO.LEFT_DRIVE_MOTOR, IO.RIGHT_DRIVE_MOTOR, defaultSpeed);
		RobotIntake = new CubeIntake(IO.LEFT_INTAKE_MOTOR, IO.RIGHT_INTAKE_MOTOR, IO.ARM_MOTOR,
				IO.LEFT_SHOOTER_MOTOR, IO.RIGHT_SHOOTER_MOTOR);
		
		//Smart Dashboard setup
		SmartDashboard.putNumber("Position", 1);
		SmartDashboard.putBoolean("Slow Speed", false);
		CameraServer.getInstance().startAutomaticCapture();
	}
	
	private void changeAutonState(int nextState) {
    	if (nextState != autonState) {
    		autonState = nextState;
    		autonStateTimer.reset();
    	}
    }
    
    public void autonomousInit() {
    	System.out.println("Autonomous Initalized!");
    	
    	//Get SmartDashboard & Game Data
    	robotPosition = (int) SmartDashboard.getNumber("Position", robotPosition);
    	//Loop to get the Game Data
    	int retries = 100;
        gameData = DriverStation.getInstance().getGameSpecificMessage();
        while (gameData.length() < 2 && retries > 0) {
            retries--;
            try {
                Thread.sleep(5);
            } catch (InterruptedException ie) {
                // Just ignore the interrupted exception
            }
            gameData = DriverStation.getInstance().getGameSpecificMessage();
        }
    	// Reset auton state to initial drive forward and reset the timer
    	autonState = AUTON_STATE_DRIVE_FORWARD;
    	if (robotPosition == 2) autonState = AUTON_STATE_DRIVE_TURN;
    	autonStateTimer = new Timer();
        autonStateTimer.start();
    }

    public void autonomousPeriodic() {
    	//Left & Right starting position
    	if (robotPosition == 1 || robotPosition == 3 || robotPosition == 4) {
    		if (gameData.length() > 0) {
    			//On our side
    			if(gameData.charAt(0) == 'L' && robotPosition == 1 ||
    					gameData.charAt(0) == 'R' && robotPosition == 3) {
    				System.out.println("L1 || R3");
    				double turnLeftSpeed = 0d;
    				double turnRightSpeed = 0d;
    				if (robotPosition == 1) {
    					turnRightSpeed = -0.3f; 
    					turnLeftSpeed = 0.5f;
    				} else {
    					turnLeftSpeed = -0.3f;
    					turnRightSpeed = 0.5f;
    				}
    				switch (autonState) {
    		    	
    		    	case AUTON_STATE_DRIVE_FORWARD: {
    		    		RobotIntake.intake(false, false, false, false);
    		    		RobotDrive.drive(0.5, 0.5);
    		    		if (autonStateTimer.hasPeriodPassed(3.6f)) {
    		    			changeAutonState(AUTON_STATE_DRIVE_TURN);
    		    		}
    		    		break;
    		    	}
    		    	
    		    	case AUTON_STATE_DRIVE_TURN: {
    					RobotDrive.drive(turnLeftSpeed, turnRightSpeed);
    					if (autonStateTimer.hasPeriodPassed(1.1f)) {
    						changeAutonState(AUTON_STATE_SHOOT);
    					}
    					break;
    				}
    		    	
    		    	case AUTON_STATE_SHOOT: {
    		    		RobotDrive.drive(0.5, 0.5);
    		    		RobotIntake.intake(false, false, false, true);
    		    		if (autonStateTimer.hasPeriodPassed(1.5f)) {
    		    			changeAutonState(AUTON_STATE_STOP);
    		    		}
    		    		break;
    		    	}
    		    	
    		    	case AUTON_STATE_STOP: {
    		    		RobotDrive.drive(0, 0);
    		    		if (autonStateTimer.hasPeriodPassed(0.5f)) {
    		    			changeAutonState(AUTON_STATE_FINISHED);
    		    		}
    		    		break;
    		    	}

    		    	case AUTON_STATE_FINISHED: {
    		    		RobotIntake.intake(false, false, false, false);
    		    		break;
    		    	}
    		    	}
    			}
    			//Not on our side || Override forwards
    			else {
    				System.out.println("R1 || L3");
    				switch (autonState) {
    				
    				case AUTON_STATE_DRIVE_FORWARD: {
    					RobotIntake.intake(false, false, false, false);
    		    		RobotDrive.drive(0.5, 0.5);
    		    		if (autonStateTimer.hasPeriodPassed(3.4f)) {
    		    			changeAutonState(AUTON_STATE_STOP);
    		    		}
    		    		break;
    		    	}
    				
    				case AUTON_STATE_STOP: {
    		    		RobotDrive.drive(0, 0);
    		    		if (autonStateTimer.hasPeriodPassed(0.5f)) {
    		    			changeAutonState(AUTON_STATE_SHOOT);
    		    		}
    		    		break;
    		    	}
    				
    				case AUTON_STATE_FINISHED: {
    		    		RobotIntake.shooter.speed = 1d;
    		    		RobotIntake.intake(false, false, false, false);
    		    		break;
    		    	}
    				
    				}
    			}
    		}
    	}
    	//Center
    	if (robotPosition == 2) {
    		if (gameData.length() > 0) {
    			//Left side
    			if(gameData.charAt(0) == 'L') {
    				System.out.println("L2");
    				switch (autonState) {
    				
    				case AUTON_STATE_DRIVE_TURN: {
    					RobotIntake.intake(false, false, false, false);
    					RobotDrive.drive(0, 0.6);
    					if (autonStateTimer.hasPeriodPassed(1.5f)) {
    						changeAutonState(AUTON_STATE_DRIVE_FORWARD);
    					}
    					break;
    				}
    				
    				case AUTON_STATE_DRIVE_FORWARD: {
    					RobotIntake.intake(false, false, false, false);
    					RobotDrive.drive(0.5, 0.5);
    					if (autonStateTimer.hasPeriodPassed(1.6f)) {
    						changeAutonState(AUTON_STATE_DRIVE_TURNBACK);
    					}
    					break;
    				}
    				
    				case AUTON_STATE_DRIVE_TURNBACK: {
    					RobotDrive.drive(0.6d, -0.25d);
    					if (autonStateTimer.hasPeriodPassed(1.3f)) {
    						changeAutonState(AUTON_STATE_DRIVE_FORWARD_BYAHAIR);
    					}
    					break;
    				}
    				
    				case AUTON_STATE_DRIVE_FORWARD_BYAHAIR: {
    					RobotDrive.drive(0.5d, 0.5d);
    					if (autonStateTimer.hasPeriodPassed(1.5f)) {
    						changeAutonState(AUTON_STATE_SHOOT);
    					}
    					break;
    				}

    				case AUTON_STATE_SHOOT: {
    		    		RobotDrive.drive(0.5, 0.5);
    		    		RobotIntake.intake(false, false, false, true);
    		    		if (autonStateTimer.hasPeriodPassed(1.5f)) {
    		    			changeAutonState(AUTON_STATE_STOP);
    		    		}
    		    		break;
    		    	}
    				
    				case AUTON_STATE_STOP: {
    		    		RobotDrive.drive(0, 0);
    		    		if (autonStateTimer.hasPeriodPassed(0.5f)) {
    		    			changeAutonState(AUTON_STATE_FINISHED);
    		    		}
    		    		break;
    		    	}
    				
    				case AUTON_STATE_FINISHED: {
    		    		RobotIntake.intake(false, false, false, false);
    		    		break;
    		    	}
    				}
    			}
    			//Right side
    			else {
    				System.out.println("R2");
    				switch (autonState) {
    				
    				case AUTON_STATE_DRIVE_TURN: {
    					RobotIntake.intake(false, false, false, false);
    					RobotDrive.drive(0.6, 0);
    					if (autonStateTimer.hasPeriodPassed(1.5f)) {
    						changeAutonState(AUTON_STATE_DRIVE_FORWARD);
    					}
    					break;
    				}
    				
    				case AUTON_STATE_DRIVE_FORWARD: {
    					RobotDrive.drive(0.5, 0.5);
    					if (autonStateTimer.hasPeriodPassed(1.55f)) {
    						changeAutonState(AUTON_STATE_DRIVE_TURNBACK);
    					}
    					break;
    				}
    				
    				case AUTON_STATE_DRIVE_TURNBACK: {
    					RobotDrive.drive(-0.25, 0.6);
    					if (autonStateTimer.hasPeriodPassed(1.3f)) {
    						changeAutonState(AUTON_STATE_DRIVE_FORWARD_BYAHAIR);
    					}
    					break;
    				}
    				
    				case AUTON_STATE_DRIVE_FORWARD_BYAHAIR: {
    					RobotDrive.drive(0.5, 0.5);
    					if (autonStateTimer.hasPeriodPassed(1.5f)) {
    						changeAutonState(AUTON_STATE_SHOOT);
    					}
    					break;
    				}

    				case AUTON_STATE_SHOOT: {
    		    		RobotDrive.drive(0.5, 0.5);
    		    		RobotIntake.intake(false, false, false, true);
    		    		if (autonStateTimer.hasPeriodPassed(1.5)) {
    		    			changeAutonState(AUTON_STATE_STOP);
    		    		}
    		    		break;
    		    	}
    				
    				case AUTON_STATE_STOP: {
    		    		RobotDrive.drive(0, 0);
    		    		if (autonStateTimer.hasPeriodPassed(0.5f)) {
    		    			changeAutonState(AUTON_STATE_FINISHED);
    		    		}
    		    		break;
    		    	}
    				
    				case AUTON_STATE_FINISHED: {
    		    		RobotIntake.intake(false, false, false, false);
    		    		break;
    		    	}
    				}
    			}
    		}
    	}
    }

	public void teleopPeriodic() {
		double leftMotorSpeed = DriveTrain.getLeftMotorSpeed(driveController, IO.DRIVE_LEFT_JOYSTICK_Y_AXIS);
		double rightMotorSpeed = DriveTrain.getRightMotorSpeed(driveController, IO.DRIVE_RIGHT_JOYSTICK_Y_AXIS);
		
		RobotDrive.changeDriveSpeed(driveController, IO.SHOOT_BUTTON_B, defaultSpeed);
		//Comment Out:
		//If you want to use the X & B buttons to shoot, not change speeds
		//RobotIntake.changeShooterSpeed(shooterController, IO.SHOOT_BUTTON_X, IO.SHOOT_BUTTON_B, 1f);
		//Comment ends here
		
		RobotDrive.drive(leftMotorSpeed, rightMotorSpeed);
		
		boolean intakingButton = CubeIntake.getButtonValue(shooterController, IO.SHOOT_BUTTON_A);
		boolean feedingButtonX = CubeIntake.getButtonValue(shooterController, IO.SHOOT_BUTTON_X);
		boolean feedingButtonY = CubeIntake.getButtonValue(shooterController, IO.SHOOT_BUTTON_Y);
		boolean feedingButtonB = CubeIntake.getButtonValue(shooterController, IO.SHOOT_BUTTON_B);
		boolean armUpButton = CubeIntake.getButtonValue(shooterController, IO.SHOOT_BUTTON_RB);
		boolean armDownButton = CubeIntake.getButtonValue(shooterController, IO.SHOOT_BUTTON_LB);
		
		CubeIntake.intake(intakingButton, feedingButtonX, feedingButtonY, feedingButtonB);
		
		RobotIntake.moveArm(armUpButton, armDownButton);
	}
	
}
