package steeringMovement;

import character.Kinematic;

public class LookWhereYoureGoing extends Align implements Behavior {
	
	/**
	 * Constructor for LookWhereYoureGoing
	 * @param character
	 * @param target
	 * @param maxAngularAcceleration
	 * @param maxRotation
	 * @param ros
	 * @param rod
	 */
    public LookWhereYoureGoing(Kinematic character, Kinematic target, float maxAngularAcceleration, float maxRotation, float ros, float rod) {
    	super(character, target, maxAngularAcceleration, maxRotation, ros, rod);
    }
    
    /**
     * 
     * @return SteeringOutput: angularAcceleration=?, acceleration=<0,0>
     */
    public SteeringOutput getSteering(){        
        // Check for a zero direction, and make no change if so
        if( character.velocity.mag() == 0 ){
        	SteeringOutput output = new SteeringOutput();
			output.angularAcc = -character.rotation;
		    return output; // return none;
        }
        
        // Otherwise set the target based on the velocity
        target.orientation =  Kinematic.getOrientationFromDirection(character.velocity);

        // 2. Delegate to align
        return super.getSteering(target);
    }

}
