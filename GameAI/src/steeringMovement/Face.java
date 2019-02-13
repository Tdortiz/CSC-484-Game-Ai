package steeringMovement;

import character.Kinematic;
import processing.core.PVector;

public class Face extends Align implements Behavior {

	/**
     * Constructor for steering face
     * @param character kinematic data
     * @param target kinematic data
     * @param maxAngularAcceleration max angular acceleration of the character
     * @param maxRotation max rotation of the character
     * @param ros radius of satisfaction
     * @param rod radius of deceleration
     */
	public Face(Kinematic character, Kinematic target, float maxAngularAcceleration, float maxRotation, float ros, float rod) {
        super(character, target, maxAngularAcceleration, maxRotation, ros, rod);
    }
	
	public SteeringOutput getSteering(){
		// 1. calculate the target to delegate to align
		
		// work out direction to the target
		PVector direction = PVector.sub( target.position, character.position );
		
		// check for zero direction, make no changes if 0
		if( direction.mag() == 0 ){
			return new SteeringOutput();
		}
		
		// Put the target together
		target.setOrientation( Kinematic.getOrientationFromDirection(direction) );
		return super.getSteering(target);
	}
}
