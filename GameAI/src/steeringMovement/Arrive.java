package steeringMovement;

import character.Kinematic;
import processing.core.PVector;

// Steering  Arrive  - 61-62
public class Arrive implements Behavior {
	
	// Static data for character and target
	protected Kinematic character;
	protected Kinematic target;
	// max speed/acceleration character can travel
	protected float maxSpeed;
	protected float maxAcceleration;
	// satisfaction radius
	protected float ros;
	// deceleration radius
	protected float rod;
	// time to target constant
	protected float timeToTarget = 1/10f;
	
	/**
	 * Constructor for steering arrive
	 * @param character
	 * @param target
	 * @param maxSpeed
	 * @param maxAcceleration
	 * @param ros
	 * @param rod
	 */
	public Arrive(Kinematic character, Kinematic target, float maxSpeed, float maxAcceleration, float ros, float rod){
		this.character = character;
		this.target = target;
		this.maxSpeed = maxSpeed;
		this.maxAcceleration = maxAcceleration;
		this.ros = ros;
		this.rod = rod;
	}
	
	public SteeringOutput getSteering(){
        return getSteering(target);
    }
	
	/**
	 * 
	 * @return SteeringOutput: acceleration=<?,?>, angularAcceleration=0
	 */
	public SteeringOutput getSteering(Kinematic target){
		// Create structure for output
		SteeringOutput steering = new SteeringOutput();
		
		// Get direction and magnitude to the target
		PVector direction = PVector.sub(target.getPosition(), character.getPosition());
		float distance = direction.mag();
		
		float targetSpeed = 0;
		// check if within radius
		if( distance <= ros ){
		    steering.acceleration = new PVector( -character.getVelocity().x, -character.getVelocity().y ); // zeros out velocity
		    return steering; // return none
		}
		
		if( distance >= rod ){
			targetSpeed = maxSpeed;
		} else {
			targetSpeed = maxSpeed * distance / rod;
		}
		
		// target velocity combines speed and direction
		PVector targetVelocity = direction.normalize().mult(targetSpeed);
		
		// acceleration tries to get to the target velocity
		steering.acceleration = PVector.sub( targetVelocity, character.getVelocity() ).div(timeToTarget);
		
		// clip if its too fast
		if( steering.acceleration.mag() > maxAcceleration){
			steering.acceleration.normalize().mult(maxAcceleration);
		}
		
		// output steering
		steering.angularAcc = 0;
        return steering;
	}

}
