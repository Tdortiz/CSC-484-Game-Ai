package kinematicMovement;

import character.Kinematic;
import steeringMovement.Behavior;
import steeringMovement.SteeringOutput;

// Kinematic Wandering - 53
public class KinematicWander implements Behavior {
	
	// Static data for character and target
	private Kinematic character;
	// Max speed the character can travel
	private float maxSpeed;
	// max rotation speed 
	private float maxRotation;
	
	public KinematicWander(Kinematic character, float maxSpeed, float maxRotation ){
		this.character = character;
		this.maxSpeed = maxSpeed;
		this.maxRotation = maxRotation;
	}
	
	public SteeringOutput getSteering(){
		// Create structure for output
		SteeringOutput steering = new SteeringOutput();
		
		// Get velocity from the vector form of the orientation
		steering.setVelocity( Kinematic.getDirectionFromOrientation(character.getOrientation()).mult(maxSpeed) );
		
		// Change our orientation randomly
		steering.setRotation( Kinematic.randomBinomial() * maxRotation );
		
		// Output steering
        return steering;
	}
}
