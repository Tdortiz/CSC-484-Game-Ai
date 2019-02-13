package steeringMovement;

import character.Kinematic;
import processing.core.PVector;

//Steering  Align  - 64-65
public class Align implements Behavior {
    // Static data for character and target
	protected Kinematic character;
	protected Kinematic target;
	// max angular acceleration and rotation of the character
	protected float maxAngularAcceleration;
	protected float maxRotation;
	// satisfaction radius
	protected float ros;
	// deceleration radius
	protected float rod;
	// time to target constant
	private float timeToTarget = 1/10f;
	
	/**
	 * Constructor for steering align
	 * @param character kinematic data
	 * @param target kinematic data
	 * @param maxAngularAcceleration max angular acceleration of the character
	 * @param maxRotation max rotation of the character
	 * @param ros radius of satisfaction
	 * @param rod radius of deceleration
	 */
	public Align(Kinematic character, Kinematic target, float maxAngularAcceleration, float maxRotation, float ros, float rod){
		this.character = character;
		this.target = target;
		this.maxAngularAcceleration = maxAngularAcceleration;
		this.maxRotation = maxRotation;
		this.ros = ros;
		this.rod = rod;
	}
	
	/**
	 * 
	 * @param target
	 * @return SteeringOutput: angularAcceleration=?, acceleration = <0,0>
	 */
	public SteeringOutput getSteering(Kinematic target){
		// Create structure for output
		SteeringOutput steering = new SteeringOutput();
		
		// Get the naive direction to the target
		float rotation = target.orientation - character.orientation;
		
		// Map the result to the [-pi, pi] interval
		rotation = Kinematic.mapToRange(rotation);
		float rotationSize = Math.abs(rotation);
		
		// check if we are there, return no steering
		float goalRotation = 0;
		if( rotationSize < ros ){
			SteeringOutput output = new SteeringOutput();
			output.angularAcc = -character.rotation;
		    return output; // return none;
			
		// check if we are outside the slow radius, if yes use max rotation
		} else if(rotationSize > rod){
			goalRotation = maxRotation;
			
		// otherwise calculate a scaled rotation
		} else {
			goalRotation = maxRotation * (rotationSize / rod );
		}
		
		// final target rotation combines speed and direction
		goalRotation *= (rotation / rotationSize);
		
		// acceleration tries to get to the target rotation
		steering.angularAcc = goalRotation-character.getRotation();
		steering.angularAcc = steering.angularAcc / timeToTarget;
		
		// check if acceleration is too great
		float angularAcceleration = Math.abs(steering.angularAcc);
		if(angularAcceleration > maxAngularAcceleration){
			steering.angularAcc = steering.angularAcc / angularAcceleration;
			steering.angularAcc = steering.angularAcc * maxAngularAcceleration;
		}
		
		// output steering
		steering.acceleration = new PVector(0,0);
        return steering;
	}

    
	@Override
    public SteeringOutput getSteering() {
        return getSteering(this.target);
    }
	
}
